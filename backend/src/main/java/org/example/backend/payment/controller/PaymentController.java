package org.example.backend.payment.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.payment.domain.Payment;
import org.example.backend.payment.domain.PaymentStatus;
import org.example.backend.payment.domain.RefundReasonType;
import org.example.backend.payment.dto.PaymentResponseDto;
import org.example.backend.payment.dto.RefundRequestDto;
import org.example.backend.payment.dto.TossPaymentConfirmRequest;
import org.example.backend.payment.dto.PaymentResponseDto;
import org.example.backend.payment.repository.PaymentRepository;
import org.example.backend.payment.service.PaymentService;
import org.example.backend.payment.service.TossRefundService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

  private final PaymentService paymentService;
  private final TossRefundService tossRefundService;
  private final PaymentRepository paymentRepository;

  @PostMapping("/toss/confirm")
  public ResponseEntity<?> confirmTossPayment(@RequestBody TossPaymentConfirmRequest request) {
    try {
      PaymentResponseDto response = paymentService.confirmPayment(
          request.getPaymentKey(),
          request.getReservationId(),
          request.getAmount()
      );
      return ResponseEntity.ok(response);

    } catch (Exception e) {
      // 콘솔에 예외 전체 출력
      System.err.println("❌ 결제 승인 처리 중 오류 발생:");
      e.printStackTrace();

      // Postman에도 오류 메시지를 반환
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("서버 오류 발생: " + e.getClass().getSimpleName() + " - " + e.getMessage());
    }
  }
  @PostMapping("/{paymentId}/cancel")
  public ResponseEntity<String> refundPayment(
      @PathVariable String paymentId,
      @RequestBody RefundRequestDto refundRequestDto) {

    if (refundRequestDto == null || refundRequestDto.getReasonMessage() == null) {
      throw new IllegalArgumentException("취소 사유는 필수입니다.");
    }

    String cancelReason = refundRequestDto.getReasonMessage();

    Payment payment = paymentRepository.findById(paymentId)
        .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

    tossRefundService.refund(payment, cancelReason); // ✅ 문자열 기반으로 넘김

    payment.setStatus(PaymentStatus.CANCELLED);
    paymentRepository.save(payment);

    return ResponseEntity.ok("환불이 완료되었습니다.");
  }
}
