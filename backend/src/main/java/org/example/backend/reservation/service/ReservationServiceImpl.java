package org.example.backend.reservation.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.backend.user.repository.UserRepository;
import org.example.backend.reservation.domain.MentoringReservation;
import org.example.backend.reservation.domain.ReservationStatus;
import org.example.backend.reservation.dto.ApproveReservationResponseDTO;
import org.example.backend.reservation.dto.ReservationRequestDto;
import org.example.backend.reservation.dto.ReservationResponseDto;
import org.example.backend.reservation.repository.ReservationRepository;
import org.example.backend.mentor.domain.MentorUser;
import org.example.backend.mentor.repository.MentorUserRepository;
import org.example.backend.room.domain.MentoringRoom;
import org.example.backend.room.repository.MentoringRoomRepository;
import org.example.backend.room.service.MentoringRoomService;
import org.example.backend.user.domain.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

  private final ReservationRepository reservationRepository;
  private final MentorUserRepository mentorUserRepository;
  private final MentoringRoomRepository mentoringRoomRepository;
  private final UserRepository userRepository;
  private final MentoringRoomService mentoringRoomService;

  @Override
  @Transactional
  public ReservationResponseDto createReservation(ReservationRequestDto requestDto, Long userId) {

    // 1. 사용자 조회
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

    // 2. 멘토 조회
    MentorUser mentor = mentorUserRepository.findById(requestDto.getMentorId())
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 멘토입니다."));

    // 3. 예약 시간 중복 체크
    boolean isOverlapping = reservationRepository.existsByMentorAndReservationTime(mentor, requestDto.getReservationTime());
    if (isOverlapping) {
      throw new IllegalStateException("이미 예약된 시간입니다.");
    }

    // 4. 예약 저장
    MentoringReservation reservation = MentoringReservation.builder()
        .mentor(mentor)
        .user(user)
        .reservationTime(requestDto.getReservationTime())
        .status(ReservationStatus.ACCEPT)
        .build();

    MentoringReservation saved = reservationRepository.save(reservation);

    // 5. 응답 DTO 생성
    return new ReservationResponseDto(
        saved.getReserveId(),
        mentor.getUser().getNickname(), // mentorName 추출
        saved.getReservationTime(),
        saved.getStatus()
    );
  }

  @Transactional
  public ApproveReservationResponseDTO approveReservationResponse(Long reservationId){
    MentoringReservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(()-> new RuntimeException("예약을 찾을 수 없습니다."));

    if(reservation.getStatus() == ReservationStatus.ACCEPT){
      throw new RuntimeException("이미 승인된 예약입니다.");
    }


    reservation.approve();

    MentoringRoom room = mentoringRoomService.createRoomFromReservation(reservation);


    return ApproveReservationResponseDTO.of(room);
  }



}
