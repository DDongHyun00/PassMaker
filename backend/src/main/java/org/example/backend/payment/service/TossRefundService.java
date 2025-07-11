package org.example.backend.payment.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.payment.domain.Payment;
import org.example.backend.payment.domain.RefundReasonType;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TossRefundService {

  private final TossPaymentClient tossPaymentClient;

  /**
   * 환불 처리 메서드 – 멘토 거절 또는 멘티 취소 시 호출
   *
   * @param payment 환불할 결제 정보
   * @param reason  환불 사유 (MENTOR_REJECT, MENTEE_CANCEL 등)
   */
  public void refund(Payment payment, RefundReasonType reason) {
    System.out.println("[환불 처리 시작] 결제 PK: " + payment.getPaymentKey());

    tossPaymentClient.requestRefund(
        payment.getPaymentKey(),
        reason.name(),            // ex) "MENTOR_REJECT"
        payment.getAmount()       // 전액 환불 기준
    );

    System.out.println("[환불 처리 완료]");
  }

  /**
   * 환불 처리 메서드 – 사용자가 입력한 사유 메시지를 그대로 Toss에 전달
   *
   * @param payment 환불할 결제 정보
   * @param cancelReason 사용자가 입력한 취소 사유 (ex. "개인 사정으로 인해 취소합니다.")
   */
  public void refund(Payment payment, String cancelReason) {
    System.out.println("[환불 처리 시작 - 사용자 메시지] 결제 PK: " + payment.getPaymentKey());

    tossPaymentClient.requestRefund(
        payment.getPaymentKey(),
        cancelReason,
        payment.getAmount()
    );

    System.out.println("[환불 처리 완료 - 사용자 메시지]");
  }
}
