package org.example.backend.payment.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.example.backend.mentor.domain.MentorUser;
import org.example.backend.mentor.repository.MentorUserRepository;
import org.example.backend.payment.domain.Payment;
import org.example.backend.payment.domain.PaymentStatus;
import org.example.backend.payment.dto.PaymentResponseDto;
import org.example.backend.payment.repository.PaymentRepository;
import org.example.backend.reservation.domain.MentoringReservation;
import org.example.backend.reservation.domain.ReservationStatus;
import org.example.backend.reservation.repository.MentoringReservationRepository;
import org.example.backend.user.domain.User;
import org.example.backend.user.repository.UserRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {

  private final WebClient tossWebClient;
  private final PaymentRepository paymentRepository;
  private final MentoringReservationRepository reservationRepository;
  private final MentorUserRepository mentorUserRepository;
  private final UserRepository userRepository;


  /**
   * ✅ 결제 성공 후 → 예약 + 결제 통합 저장
   */
  public PaymentResponseDto reserveAfterPayment(Long mentorId, String orderId, String paymentKey, int amount, LocalDateTime reservationTime, User user
  ) {
    System.out.println("=== [예약 + 결제 통합 처리 시작] ===");
    System.out.println(">>> paymentKey: " + paymentKey);
    System.out.println(">>> orderId: " + orderId);
    System.out.println(">>> amount: " + amount);
    System.out.println(">>> reserTime: " + reservationTime);





    // ✅ 2. 중복 체크
    boolean exists = reservationRepository.existsByMentorIdAndTimeIgnoringSeconds(mentorId, reservationTime);
    if (exists) {
      throw new IllegalStateException("이미 예약된 시간입니다.");
    }

    // ✅ 3. 예약 생성
    MentorUser mentor = mentorUserRepository.findById(mentorId)
        .orElseThrow(() -> new IllegalArgumentException("멘토 정보를 찾을 수 없습니다."));

    // TODO: 실제 사용자 인증 연결 필요
    User mentee = user;

    MentoringReservation reservation = MentoringReservation.builder()
        .mentor(mentor)
        .user(mentee)
        .reservationTime(reservationTime)
        .status(ReservationStatus.WAITING)
        .build();

    reservationRepository.save(reservation);

    // ✅ 4. 결제 승인 요청
    Map<String, Object> requestBody = Map.of(
        "paymentKey", paymentKey,
        "orderId", orderId,
        "amount", amount
    );

    JsonNode tossResponse = confirmTossPaymentWithRetry(requestBody);

    // ✅ 5. 결제 정보 저장
    Payment payment = Payment.builder()
        .payId(orderId)
        .paymentKey(paymentKey)
        .amount(amount)
        .approvedAt(LocalDateTime.now())
        .reservation(reservation)
        .status(PaymentStatus.PAID)
        .build();

    paymentRepository.save(payment);

    // ✅ 6. 응답 DTO 생성
    return PaymentResponseDto.builder()
        .payId(payment.getPayId())
        .amount(payment.getAmount())
        .status(payment.getStatus().name())
        .reservationStatus(reservation.getStatus().name())
        .approvedAt(payment.getApprovedAt())
        .reservationTime(reservationTime)
        .mentorNickname(mentor.getUser().getNickname())
        .menteeNickname(mentee.getNickname())
        .build();
  }

  private JsonNode confirmTossPaymentWithRetry(Map<String, Object> requestBody) {
    int retryCount = 0;
    int maxRetry = 3;
    int retryDelayMillis = 1000;

    while (retryCount < maxRetry) {
      JsonNode response = tossWebClient.post()
          .uri("/payments/confirm")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(requestBody)
          .exchangeToMono(res -> res.bodyToMono(JsonNode.class))
          .block();

      if (response != null) {
        System.out.println(">>> Toss 응답(" + (retryCount + 1) + "회차): " + response.toPrettyString());

        if (response.has("code")) {
          String errorCode = response.get("code").asText();
          if ("ALREADY_PROCESSED_PAYMENT".equals(errorCode)) {
            System.out.println("✅ Toss: 이미 처리된 결제. 성공으로 간주하고 종료.");
            return response;
          }
        }

        if (response.has("status") && "DONE".equals(response.get("status").asText())) {
          return response;
        }
      }

      retryCount++;
      System.out.println("🔁 Toss 결제 재시도 " + retryCount + "회");

      try {
        Thread.sleep(retryDelayMillis);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new RuntimeException("Toss 결제 재시도 중 인터럽트됨");
      }
    }

    throw new RuntimeException("Toss 결제 실패 또는 최대 재시도 초과");
  }
}
