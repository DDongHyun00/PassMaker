package org.example.backend.reservation.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.reservation.dto.ApproveReservationResponseDTO;
import org.example.backend.reservation.dto.ReservationActionRequestDTO;
import org.example.backend.reservation.dto.ReservationDto;
import org.example.backend.reservation.dto.ReservationRequestDto;
import org.example.backend.reservation.dto.ReservationResponseDto;
import org.example.backend.reservation.dto.ReservationCancelRequestDto; // 추가된 import
import org.example.backend.reservation.service.ReservationService;
import org.example.backend.auth.domain.CustomUserDetails; // 사용자 정의 UserDetails
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List; // ✅ 추가: List import

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

  private final ReservationService reservationService;

  @PostMapping
  public ResponseEntity<ReservationResponseDto> createReservation(
      @RequestBody ReservationRequestDto requestDto,
      @AuthenticationPrincipal CustomUserDetails userDetails
  ) {
    Long userId = userDetails.getUserId(); // JWT에서 추출된 사용자 ID
    ReservationResponseDto response = reservationService.createReservation(requestDto, userId);
    return ResponseEntity.ok(response);//예약 완료 응답 반환
  }

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

    reservationService.handleReservationAction(
        reservationId,
        requestDto.getAction(),
        userDetails.getUserId()  // 멘토 ID
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

}
