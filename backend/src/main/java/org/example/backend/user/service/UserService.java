package org.example.backend.user.service;

import org.example.backend.reservation.dto.ReservationDto;

import java.util.List;

public interface UserService {
    List<ReservationDto> getMyReservations(Long userId);
}
