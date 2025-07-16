package org.example.backend.reservation.service;

import org.example.backend.reservation.dto.ApproveReservationResponseDTO;
import org.example.backend.reservation.dto.ReservationDto;
import org.example.backend.reservation.dto.ReservationRequestDto;
import org.example.backend.reservation.dto.ReservationResponseDto;
import org.example.backend.reservation.dto.ReservationCancelRequestDto;

import java.time.LocalDateTime;

public interface ReservationService {
  ReservationResponseDto createReservation(ReservationRequestDto requestDto, Long userId);
  ApproveReservationResponseDTO approveReservationResponse(Long reservationId);
  String handleReservationAction(Long reservationId, String action, Long mentorUserId);
  void cancelReservation(Long reservationId, Long userId);
  ReservationDto getReservationStatus(Long reservationId, Long userId);
  // ✅ 추가: 특정 사용자의 모든 예약 내역을 조회하는 메서드
  java.util.List<ReservationDto> getAllReservations(Long userId);

  /**
   * ✅ 멘토 ID + 예약 시간 기준 중복 예약 여부 확인
   */
  boolean checkDuplicateReservation(Long mentorId, LocalDateTime reservationTime);
}
