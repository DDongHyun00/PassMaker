package org.example.backend.reservation.service;

import org.example.backend.reservation.dto.ReservationRequestDto;
import org.example.backend.reservation.dto.ReservationResponseDto;

public interface ReservationService {
  ReservationResponseDto createReservation(ReservationRequestDto requestDto, Long userId);
}
