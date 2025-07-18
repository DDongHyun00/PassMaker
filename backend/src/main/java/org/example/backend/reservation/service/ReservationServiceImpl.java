package org.example.backend.reservation.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.backend.reservation.dto.*;
import org.example.backend.user.repository.UserRepository;
import org.example.backend.payment.domain.Payment;
import org.example.backend.payment.domain.PaymentStatus;
import org.example.backend.payment.domain.RefundReasonType;
import org.example.backend.payment.service.TossRefundService;
import org.example.backend.reservation.domain.MentoringReservation;
import org.example.backend.reservation.domain.ReservationStatus;
import org.example.backend.reservation.repository.ReservationRepository;
import org.example.backend.reservation.repository.MentoringReservationRepository;
import org.example.backend.mentor.domain.MentorUser;
import org.example.backend.mentor.repository.MentorUserRepository;
import org.example.backend.room.domain.MentoringRoom;
import org.example.backend.room.repository.MentoringRoomRepository;
import org.example.backend.room.service.MentoringRoomService;
import org.example.backend.user.domain.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final MentorUserRepository mentorUserRepository;
    private final MentoringRoomRepository mentoringRoomRepository;
    private final UserRepository userRepository;
    private final TossRefundService tossRefundService;  // 추가된 의존성 주입
    private final MentoringRoomService mentoringRoomService;
    private final MentoringReservationRepository mentoringReservationRepository;


    @Override
    @Transactional
    public ReservationResponseDto createReservation(ReservationRequestDto requestDto, Long userId) {

        // 1. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 2. 멘토 조회
        MentorUser mentor = mentorUserRepository.findById(requestDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 멘토입니다."));

        // 3. 예약 시간 중복 체크
        boolean isOverlapping = reservationRepository.existsByMentorAndReservationTime(
                mentor, requestDto.getReservationTime());
        if (isOverlapping) {
            throw new IllegalStateException("이미 예약된 시간입니다.");
        }
    if (mentor.getUser().getUserId().equals(user.getUserId())) {
      throw new AccessDeniedException("자기 자신에게는 예약할 수 없습니다.");
    }

        // 4. 예약 저장
        MentoringReservation reservation = MentoringReservation.builder()
                .mentor(mentor)
                .user(user)
                .reservationTime(requestDto.getReservationTime())
                .status(ReservationStatus.WAITING)
                .build();

        MentoringReservation saved = reservationRepository.save(reservation);

        // 5. 응답 DTO 생성
        return new ReservationResponseDto(
                saved.getReserveId(),                            // 1. 예약 ID
                saved.getMentor().getId(),                       // 2. 멘토 ID
                saved.getUser().getUserId(),                     // 3. 멘티 ID
                saved.getMentor().getUser().getNickname(),       // 4. 멘토 이름
                saved.getReservationTime(),                      // 5. 예약 시간
                saved.getStatus(),                               // 6. 예약 상태
                saved.getPayment() != null
                        ? saved.getPayment().getStatus().name()      // 7. 결제 상태
                        : "UNPAID"
        );
    }


  @Override
  public boolean checkDuplicateReservation(Long mentorId, LocalDateTime reservationTime) {
    return mentoringReservationRepository.existsByMentorIdAndTimeIgnoringSeconds(mentorId, reservationTime);
  }

  @Transactional
  public ApproveReservationResponseDTO approveReservationResponse(Long reservationId) {
    MentoringReservation reservation = reservationRepository.findById(reservationId)
        .orElseThrow(() -> new RuntimeException("예약을 찾을 수 없습니다."));

        if (reservation.getStatus() == ReservationStatus.ACCEPT) {
            throw new RuntimeException("이미 승인된 예약입니다.");
        }

        reservation.setStatus(ReservationStatus.ACCEPT);

        MentoringRoom room = mentoringRoomService.createRoomFromReservation(reservation);

        mentoringRoomRepository.save(room);

        return ApproveReservationResponseDTO.of(room);
    }


  @Transactional
  public String handleReservationAction(Long reservationId, String action, Long mentorUserId) {
    MentoringReservation reservation = reservationRepository.findById(reservationId)
        .orElseThrow(() -> new IllegalArgumentException("해당 예약이 존재하지 않습니다."));

        if (!reservation.getMentor().getId().equals(mentorUserId)) {
            throw new AccessDeniedException("예약에 대한 권한이 없습니다.");
        }

        if (action.equalsIgnoreCase("ACCEPT")) {
            reservation.setStatus(ReservationStatus.ACCEPT);
            reservationRepository.save(reservation);
            return "예약이 수락되었습니다.";

        } else if (action.equalsIgnoreCase("REJECT")) {
            reservation.setStatus(ReservationStatus.REJECT);

            Payment payment = reservation.getPayment();
            tossRefundService.refund(payment, RefundReasonType.MENTOR_REJECT); // ✅ 환불 API 호출
            payment.setStatus(PaymentStatus.CANCELLED); // ✅ 지금은 메모리 상에서만

            reservationRepository.save(reservation);
            return "예약이 거절되어 환불이 완료되었습니다.";

        } else {
            throw new IllegalArgumentException("유효하지 않은 action 값입니다: " + action);
        }
    }

    // ✅ 멘티가 직접 취소하는 API (30분 전까지 가능, Toss 환불 포함)
    @Transactional
    public void cancelReservation(Long reservationId, Long userId) {
        MentoringReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 예약이 존재하지 않습니다."));

        if (!reservation.getUser().getUserId().equals(userId)) {
            throw new AccessDeniedException("본인의 예약만 취소할 수 있습니다.");
        }

        if (reservation.getStatus() != ReservationStatus.ACCEPT) {
            throw new IllegalStateException("결제 완료된 예약만 취소할 수 있습니다.");
        }

        if (reservation.getReservationTime().isBefore(LocalDateTime.now().plusMinutes(30))) {
            throw new IllegalStateException("예약 30분 전까지만 취소할 수 있습니다.");
        }

        Payment payment = reservation.getPayment();
        tossRefundService.refund(payment, RefundReasonType.MENTEE_CANCEL);
        payment.setStatus(PaymentStatus.CANCELLED);            // ✅ 환불 처리됨
        reservation.setStatus(ReservationStatus.CANCELLED);    // ✅ 예약 취소됨

        reservationRepository.save(reservation);
    }

    @Override
    @Transactional
    public ReservationDto getReservationStatus(Long reservationId, Long userId) {
        MentoringReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 예약이 존재하지 않습니다."));

        if (!reservation.getUser().getUserId().equals(userId)) {
            throw new AccessDeniedException("본인의 예약만 조회할 수 있습니다.");
        }

        // ReservationDto는 ReservationResponseDto와 유사한 구조를 가지고 있으므로,
        // ReservationResponseDto를 ReservationDto로 변환하여 반환합니다.
        return new ReservationDto(
                reservation.getReserveId(),
                reservation.getMentor().getUser().getNickname(),
                reservation.getReservationTime(),
                reservation.getStatus(),
                reservation.getStatus().name(), // statusLabel
                getReservationStatusColor(reservation.getStatus()) // statusColor
        );
    }

    private String getReservationStatusColor(ReservationStatus status) {
        switch (status) {
            case ACCEPT:
                return "green";
            case REJECT:
                return "red";
            case WAITING:
                return "orange";
            case CANCELLED:
                return "gray";
            default:
                return "black"; // Default color for unknown status
        }
    }

    private String generateRoomCode() {
        return RandomStringUtils.randomAlphanumeric(6).toUpperCase();
    }

    // ✅ 추가: 특정 사용자의 모든 예약 내역을 조회하는 메서드 구현
    @Override
    @Transactional
    public java.util.List<ReservationDto> getAllReservations(Long userId) {
        // 1. 사용자 조회 (존재하지 않는 사용자일 경우 예외 처리)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 2. 해당 사용자의 모든 예약 내역 조회
        java.util.List<MentoringReservation> reservations = reservationRepository.findByUser(user);

        // 3. MentoringReservation 리스트를 ReservationDto 리스트로 변환하여 반환
        return reservations.stream()
                .map(reservation -> new ReservationDto(
                        reservation.getReserveId(),
                        reservation.getMentor().getUser().getNickname(),
                        reservation.getReservationTime(),
                        reservation.getStatus(),
                        reservation.getStatus().name(),
                        getReservationStatusColor(reservation.getStatus())))
                .collect(java.util.stream.Collectors.toList());
    }


    public List<ReservationEnterDto> getAcceptedReservationsWithRoom(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        List<MentoringReservation> reservations =
                reservationRepository.findByUserAndStatus(user, ReservationStatus.ACCEPT);

        return reservations.stream()
                .filter(r -> r.getRoom() != null)
                .map(r -> {
                    MentoringRoom room = r.getRoom();
                    return new ReservationEnterDto(
                            r.getReserveId(),
                            r.getMentor().getUser().getNickname(),
                            room.getStartedAt(),
                            room.getEndedAt(),
                            room.getRoomId(),
                            room.getRoomCode()
                    );
                })
                .collect(Collectors.toList());
    }

}
