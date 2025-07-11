package org.example.backend.reservation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationActionRequestDTO {
  private String action; // "ACCEPT" 또는 "REJECT"
}
