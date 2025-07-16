package org.example.backend.payment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class TossPaymentReserveRequest {

  private Long mentorId;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") // ISO 8601 형식
  private LocalDateTime reservationTime;

  private String paymentKey; // Toss 결제 완료 후 전달되는 키
  private String orderId;    // 예: "reserve_1_20250715165000"
  private int amount;        // 결제 금액 (ex: 33000)
}
