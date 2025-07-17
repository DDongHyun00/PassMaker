package org.example.backend.payment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class TossPaymentReserveRequest {

  private Long mentorId;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime reservationTime;

  private String paymentKey;
  private String orderId;
  private int amount;
  private String nickname; // ✅ 추가 (프론트에서 넘기는 값과 일치)
}
