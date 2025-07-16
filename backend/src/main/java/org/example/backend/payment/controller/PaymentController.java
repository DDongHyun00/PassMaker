package org.example.backend.payment.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.auth.domain.CustomUserDetails;
import org.example.backend.payment.domain.Payment;
import org.example.backend.payment.domain.PaymentStatus;
import org.example.backend.payment.dto.PaymentResponseDto;
import org.example.backend.payment.dto.RefundRequestDto;
import org.example.backend.payment.dto.TossPaymentReserveRequest;
import org.example.backend.payment.repository.PaymentRepository;
import org.example.backend.payment.service.PaymentService;
import org.example.backend.payment.service.TossRefundService;
import org.example.backend.user.domain.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

  private final PaymentService paymentService;
  private final TossRefundService tossRefundService;
  private final PaymentRepository paymentRepository;

  /**
   * ✅ 신규: 예약 + 결제 승인 통합 처리 (orderId 사용)
   */
  @PostMapping("/toss/reserve")
  public ResponseEntity<?> reserveAfterPayment(
      @RequestBody TossPaymentReserveRequest request,
      @AuthenticationPrincipal CustomUserDetails customUserDetails // ✅ 인증 객체
  ) {
    try {
      User user = customUserDetails.getUser(); // ✅ 실제 로그인한 User Entity

      PaymentResponseDto response = paymentService.reserveAfterPayment(
          request.getMentorId(),
          request.getOrderId(),
          request.getPaymentKey(),
          request.getAmount(),
          request.getReservationTime(),
          user
      );

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("예약/결제 처리 실패: " + e.getMessage());
    }
  }

  /**
   * ✅ 환불 요청
   */
  @PostMapping("/{paymentId}/cancel")
  public ResponseEntity<String> refundPayment(
      @PathVariable String paymentId,
      @RequestBody RefundRequestDto refundRequestDto
  ) {
    if (refundRequestDto == null || refundRequestDto.getReasonMessage() == null) {
      throw new IllegalArgumentException("취소 사유는 필수입니다.");
    }

    String cancelReason = refundRequestDto.getReasonMessage();

    Payment payment = paymentRepository.findById(paymentId)
        .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

    tossRefundService.refund(payment, cancelReason);
    payment.setStatus(PaymentStatus.CANCELLED);
    paymentRepository.save(payment);

    return ResponseEntity.ok("환불이 완료되었습니다.");
  }
}
