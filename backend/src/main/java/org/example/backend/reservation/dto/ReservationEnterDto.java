package org.example.backend.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.backend.reservation.domain.ReservationStatus;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReservationEnterDto {
    private Long reservationId;
    private String mentorNickname;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private Long roomId;
    private String roomCode;
    private ReservationStatus status;
    private LocalDateTime reservationTime;
}