package org.example.backend.user.service;

import org.example.backend.reservation.dto.ReservationDto;
import org.example.backend.user.dto.FindEmailRequestDto;
import org.example.backend.user.dto.FindEmailResponseDto;
import org.example.backend.user.dto.ResetPasswordRequestDto;
import org.example.backend.user.dto.ResetPasswordResponseDto;

import java.util.List;

public interface UserService {
    List<ReservationDto> getMyReservations(Long userId);
    FindEmailResponseDto findEmail(FindEmailRequestDto requestDto);
    ResetPasswordResponseDto resetPassword(ResetPasswordRequestDto requestDto);

}
