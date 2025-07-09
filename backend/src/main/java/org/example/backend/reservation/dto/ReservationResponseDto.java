package org.example.backend.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.backend.reservation.domain.ReservationStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ReservationResponseDto {

  private Long reservationId;             // 예약 ID
  private String mentorName;              // 멘토 이름 (예: 장유빈)
  private LocalDateTime reservationTime;  // 예약 시간
  private ReservationStatus status;       // WAITING, COMPLETED 등
}
