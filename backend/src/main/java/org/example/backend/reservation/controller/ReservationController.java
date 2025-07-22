package org.example.backend.reservation.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.auth.domain.CustomUserDetails;
import org.example.backend.reservation.dto.*;
import org.example.backend.reservation.repository.MentoringReservationRepository;
import org.example.backend.reservation.service.ReservationService;
import org.example.backend.reservation.service.ReservationServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

  private final ReservationService reservationService;
  private final MentoringReservationRepository mentoringReservationRepository;
  private final ReservationServiceImpl reservationServiceImpl;

  // ❌ 예약 먼저 저장하는 API 제거 (결제 성공 후에만 예약 저장하도록 일원화)
//  @PostMapping
//  public ResponseEntity<ReservationResponseDto> createReservation(
//      @RequestBody ReservationRequestDto requestDto,
//      @AuthenticationPrincipal CustomUserDetails userDetails
//  ) {
//    Long userId = userDetails.getUserId();
//    ReservationResponseDto response = reservationService.createReservation(requestDto, userId);
//    return ResponseEntity.ok(response);
//  }

  @PatchMapping("/{reservationId}/approve")
  public ResponseEntity<ApproveReservationResponseDTO> approve(@PathVariable Long reservationId) {
    ApproveReservationResponseDTO response = reservationService.approveReservationResponse(reservationId);
    return ResponseEntity.ok(response);
  }

  @PatchMapping("/{reservationId}/action")
  public ResponseEntity<Void> handleReservationAction(
      @PathVariable Long reservationId,
      @RequestBody ReservationActionRequestDTO requestDto,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    String action = requestDto.getAction().toLowerCase();
    if (!action.equals("accept") && !action.equals("reject")) {
      throw new IllegalArgumentException("지원하지 않는 예약 액션입니다. (accept 또는 reject만 가능)");
    }

    reservationService.handleReservationAction(
        reservationId,
        action,
        userDetails.getUserId()
    );

    return ResponseEntity.ok().build();
  }

  // ✅ 멘티가 본인의 예약을 취소할 수 있는 API
  @DeleteMapping("/{reservationId}/cancel") // ✅ URI 변경: /api/reservations 제거
  public ResponseEntity<?> cancelReservation(
      @PathVariable Long reservationId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    // 서비스 계층으로 위임 (예약 ID와 현재 로그인된 사용자 ID 전달)
    reservationService.cancelReservation(reservationId, userDetails.getUserId());

    // 성공 응답 반환
    return ResponseEntity.ok("예약이 취소되었습니다.");
  }

  // ✅ 변경: 특정 예약 상태 조회 (MPE-001)
  @GetMapping("/{reservationId}") // ✅ URI 변경: /status 제거
  public ResponseEntity<ReservationDto> getReservationById(
      @PathVariable Long reservationId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    ReservationDto reservationStatus = reservationService.getReservationStatus(reservationId, userDetails.getUserId());
    return ResponseEntity.ok(reservationStatus);
  }

  // ✅ 추가: 전체 예약 내역 조회
  @GetMapping
  public ResponseEntity<List<ReservationDto>> getAllReservations(
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    List<ReservationDto> reservations = reservationService.getAllReservations(userDetails.getUserId());
    return ResponseEntity.ok(reservations);
  }

  @GetMapping("/mentor/{mentorId}/unavailable-times")
  public ResponseEntity<List<String>> getUnavailableTimes(@PathVariable Long mentorId) {
    List<String> unavailableTimes = mentoringReservationRepository.findById(mentorId)
        .stream()
        .map(r -> r.getReservationTime().toString())
        .collect(Collectors.toList());

    return ResponseEntity.ok(unavailableTimes);
  }

  /**
   * ✅ 중복 예약 체크 API - 프론트에서 결제 전에 호출
   */
  @PostMapping("/check-duplicate")
  public ResponseEntity<?> checkDuplicateReservation(@RequestBody Map<String, String> payload) {
    Long mentorId = Long.valueOf(payload.get("mentorId"));
    LocalDateTime reservationTime = LocalDateTime.parse(payload.get("reservationTime"));

    boolean exists = reservationService.checkDuplicateReservation(mentorId, reservationTime);

    if (exists) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 예약된 시간입니다.");
    }
    return ResponseEntity.ok("예약 가능");
  }


  @GetMapping("/enterable")
  public ResponseEntity<List<ReservationEnterDto>> getEnterableReservations(
          @AuthenticationPrincipal CustomUserDetails userDetails) {

    List<ReservationEnterDto> reservations =
            reservationServiceImpl.getAcceptedReservationsWithRoom(userDetails.getUserId());

    return ResponseEntity.ok(reservations);
  }

  @PatchMapping("/{reservationId}/cancel")
  public ResponseEntity<Void> cancelReservation(@PathVariable Long reservationId) {
    reservationService.cancelReservation(reservationId);
    return ResponseEntity.ok().build();
  }

}
