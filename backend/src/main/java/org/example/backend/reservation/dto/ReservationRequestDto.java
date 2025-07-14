package org.example.backend.reservation.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReservationRequestDto {

  private Long Id;                 // 예약할 멘토 ID
  private LocalDateTime reservationTime; // 예약  시각
}
