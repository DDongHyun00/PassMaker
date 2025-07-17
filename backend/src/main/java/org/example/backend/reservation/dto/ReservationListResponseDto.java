package org.example.backend.reservation.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.backend.reservation.domain.MentoringReservation;
import org.example.backend.reservation.domain.ReservationStatus;

import java.time.LocalDateTime;

@Getter
public class ReservationListResponseDto {
    private final Long reservationId;
    private final String menteeName;
    private final LocalDateTime reservationTime;
    private final ReservationStatus status;

    @Builder
    public ReservationListResponseDto(MentoringReservation reservation) {
        this.reservationId = reservation.getReserveId();
        this.menteeName = reservation.getUser().getNickname(); // Mentee 닉네임
        this.reservationTime = reservation.getReservationTime();
        this.status = reservation.getStatus();
    }
}
