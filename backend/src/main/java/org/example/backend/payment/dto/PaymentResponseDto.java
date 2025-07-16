package org.example.backend.payment.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentResponseDto {
  private String payId;
  private int amount;
  private String status; // 결제 상태 (PAID / CANCELED / FAILED)
  private LocalDateTime approvedAt;
  LocalDateTime reservationTime;

  private String mentorNickname;
  private String menteeNickname;

  private String reservationStatus; // 예약 상태 (WAITING / ACCEPT / REJECT / CANCELLED)
}
