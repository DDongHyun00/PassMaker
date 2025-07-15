package org.example.backend.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.backend.reservation.domain.ReservationStatus;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDto {
    private Long reservationId;
    private String mentorName;
    private LocalDateTime reservationTime;
    private ReservationStatus status;
    private String statusLabel;
    private String statusColor;
}
