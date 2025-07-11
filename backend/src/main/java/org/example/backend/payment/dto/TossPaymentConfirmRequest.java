package org.example.backend.payment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TossPaymentConfirmRequest {
    private String paymentKey;
    private Long reservationId;  // ✅ 이걸 기반으로 orderId 대신 쓰는 것
    private int amount;
}
