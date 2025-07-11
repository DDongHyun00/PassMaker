package org.example.backend.payment.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.example.backend.payment.domain.Payment;
import org.example.backend.payment.domain.PaymentStatus;
import org.example.backend.payment.dto.PaymentResponseDto;
import org.example.backend.payment.repository.PaymentRepository;
import org.example.backend.reservation.domain.MentoringReservation;
import org.example.backend.reservation.domain.ReservationStatus;
import org.example.backend.reservation.repository.MentoringReservationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;

@Service
//@RequiredArgsConstructor
public class PaymentService {

  private final PaymentRepository paymentRepository;
  private final MentoringReservationRepository reservationRepository;

  private final WebClient tossWebClient;
  private final String tossSecretKey;

  public PaymentService(
      PaymentRepository paymentRepository,
      MentoringReservationRepository reservationRepository,
      @Value("${toss.secret-key}") String tossSecretKey
  ) {
    this.paymentRepository = paymentRepository;
    this.reservationRepository = reservationRepository;
    this.tossSecretKey = tossSecretKey;

    this.tossWebClient = WebClient.builder()
        .baseUrl("https://api.tosspayments.com/v1")
        .defaultHeader(HttpHeaders.AUTHORIZATION,
            "Basic " + Base64.getEncoder().encodeToString((tossSecretKey + ":").getBytes()))
        .build();
  }

  public PaymentResponseDto confirmPayment(String paymentKey, Long reservationId, int amount) {
    System.out.println("=== [1단계] 결제 확인 요청 진입 ===");
    System.out.println(">>> paymentKey: " + paymentKey);
    System.out.println(">>> reservationId: " + reservationId);
    System.out.println(">>> amount: " + amount);
    System.out.println(">>> orderId 조합 값: reserve_" + reservationId);

    Map<String, Object> requestBody = Map.of(
        "paymentKey", paymentKey,
        "orderId", "reserve_" + reservationId,
        "amount", amount
    );
    System.out.println(">>> RequestBody: " + requestBody);

    JsonNode tossResponse = tossWebClient.post()
        .uri("/payments/confirm")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(requestBody)
        .retrieve()
        .bodyToMono(JsonNode.class)
        .block();

    System.out.println("=== Toss 응답 완료 ===");
    System.out.println(">>> tossResponse: " + tossResponse);

    MentoringReservation reservation = reservationRepository.findById(reservationId)
        .orElseThrow(() -> new RuntimeException("예약 정보가 존재하지 않습니다."));

    Payment payment = Payment.builder()
        .payId(reservationId.toString())
        .paymentKey(paymentKey)
        .amount(amount)
        .approvedAt(LocalDateTime.now())
        .reservation(reservation)
        .status(PaymentStatus.PAID)
        .build();
    paymentRepository.save(payment);

    reservation.setStatus(ReservationStatus.PAID);
    reservationRepository.save(reservation);

    System.out.println("=== 결제 저장 및 상태 변경 완료 ===");

    // ✅ PaymentResponseDto 반환
    return PaymentResponseDto.builder()
        .payId(payment.getPayId())
        .amount(payment.getAmount())
        .status(payment.getStatus().name())
        .approvedAt(payment.getApprovedAt())
        .reservationId(reservation.getReserveId())
        .mentorNickname(reservation.getMentor().getUser().getNickname())
        .menteeNickname(reservation.getUser().getNickname())
        .build();

  }
}
