package org.example.backend.reservation.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.reservation.dto.ReservationRequestDto;
import org.example.backend.reservation.dto.ReservationResponseDto;
import org.example.backend.reservation.service.ReservationService;
import org.example.backend.auth.domain.CustomUserDetails; // 사용자 정의 UserDetails
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
    return ResponseEntity.ok(response);//✅ 예약 완료 응답 반환
  }
}
