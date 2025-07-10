package org.example.backend.reservation.service;

import org.example.backend.reservation.dto.ApproveReservationResponseDTO;
import org.example.backend.reservation.dto.ReservationRequestDto;
import org.example.backend.reservation.dto.ReservationResponseDto;

public interface ReservationService {
  ReservationResponseDto createReservation(ReservationRequestDto requestDto, Long userId);
  ApproveReservationResponseDTO approveReservationResponse(Long reservationId);
}
