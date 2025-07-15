package org.example.backend.reservation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReservationCancelRequestDto {
    private String cancelReason;
}
