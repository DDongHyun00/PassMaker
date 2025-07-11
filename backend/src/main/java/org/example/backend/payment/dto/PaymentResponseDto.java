package org.example.backend.payment.dto;


import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentResponseDto {
  private String payId;
  private int amount;
  private String status;
  private LocalDateTime approvedAt;
  private Long reservationId;

  private String mentorNickname;
  private String menteeNickname;
}
