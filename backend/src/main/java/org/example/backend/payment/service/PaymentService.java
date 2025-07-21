//package org.example.backend.payment.service;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import lombok.RequiredArgsConstructor;
//import org.example.backend.mentor.domain.MentorUser;
//import org.example.backend.mentor.repository.MentorUserRepository;
//import org.example.backend.payment.domain.Payment;
//import org.example.backend.payment.domain.PaymentStatus;
//import org.example.backend.payment.dto.PaymentResponseDto;
//import org.example.backend.payment.repository.PaymentRepository;
//import org.example.backend.reservation.domain.MentoringReservation;
//import org.example.backend.reservation.domain.ReservationStatus;
//import org.example.backend.reservation.repository.MentoringReservationRepository;
//import org.example.backend.user.domain.User;
//import org.example.backend.user.repository.UserRepository;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;
//
//import java.time.LocalDateTime;
//import java.util.Map;
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class PaymentService {
//
//  private final WebClient tossWebClient;
//  private final PaymentRepository paymentRepository;
//  private final MentoringReservationRepository reservationRepository;
//  private final MentorUserRepository mentorUserRepository;
//  private final UserRepository userRepository;
//
//
//  /**
//   * ✅ 결제 성공 후 → 예약 + 결제 통합 저장
//   */
//  public PaymentResponseDto reserveAfterPayment(
//      Long mentorId,
//      String orderId,
//      String paymentKey,
//      int amount,
//      LocalDateTime reservationTime,
//      User user
//  ) {
//    System.out.println("=== [예약 + 결제 통합 처리 시작] ===");
//    System.out.println(">>> paymentKey: " + paymentKey);
//    System.out.println(">>> orderId: " + orderId);
//    System.out.println(">>> amount: " + amount);
//    System.out.println(">>> reserTime: " + reservationTime);
//
//    // ✅ 1. 멱등성 처리: 이미 저장된 결제 정보가 있으면 다시 처리하지 않음
//    Optional<Payment> existing = paymentRepository.findByPaymentKey(paymentKey);
//    if (existing.isPresent()) {
//      System.out.println("✅ 이미 처리된 결제 요청입니다. 예약 중복 저장 방지.");
//      Payment payment = existing.get();
//      MentoringReservation reservation = payment.getReservation();
//
//      return PaymentResponseDto.builder()
//          .payId(payment.getPayId())
//          .amount(payment.getAmount())
//          .status(payment.getStatus().name())
//          .reservationStatus(reservation.getStatus().name())
//          .approvedAt(payment.getApprovedAt())
//          .reservationTime(reservation.getReservationTime())
//          .mentorNickname(reservation.getMentor().getUser().getNickname())
//          .menteeNickname(reservation.getUser().getNickname())
//          .reserveId(reservation.getReserveId())
//          .build();
//    }
//
//    // ✅ 2. Toss 결제 승인 요청 (이제 이게 먼저!)
//    Map<String, Object> requestBody = Map.of(
//        "paymentKey", paymentKey,
//        "orderId", orderId,
//        "amount", amount
//    );
//    JsonNode tossResponse = confirmTossPaymentWithRetry(requestBody);
//
//    // ✅ 3. 예약 중복 체크 (결제 성공 후 확인)
//    boolean exists = reservationRepository.existsByMentorIdAndTimeIgnoringSeconds(mentorId, reservationTime);
//    if (exists) {
//      throw new IllegalStateException("이미 예약된 시간입니다.");
//    }
//
//    // ✅ 4. 예약 생성
//    MentorUser mentor = mentorUserRepository.findById(mentorId)
//        .orElseThrow(() -> new IllegalArgumentException("멘토 정보를 찾을 수 없습니다."));
//    User mentee = user;
//
//    MentoringReservation reservation = MentoringReservation.builder()
//        .mentor(mentor)
//        .user(mentee)
//        .reservationTime(reservationTime)
//        .status(ReservationStatus.WAITING)
//        .build();
//
//    reservationRepository.save(reservation);
//
//    // ✅ 5. 결제 정보 저장
//    Payment payment = Payment.builder()
//        .payId(orderId)
//        .paymentKey(paymentKey)
//        .amount(amount)
//        .approvedAt(LocalDateTime.now())
//        .reservation(reservation)
//        .status(PaymentStatus.PAID)
//        .build();
//
//    paymentRepository.save(payment);
//
//    // ✅ 6. 응답 DTO 생성
//    return PaymentResponseDto.builder()
//        .payId(payment.getPayId())
//        .amount(payment.getAmount())
//        .status(payment.getStatus().name())
//        .reservationStatus(reservation.getStatus().name())
//        .approvedAt(payment.getApprovedAt())
//        .reservationTime(reservation.getReservationTime())
//        .mentorNickname(mentor.getUser().getNickname())
//        .menteeNickname(mentee.getNickname())
//        .reserveId(reservation.getReserveId())
//        .build();
//  }
//
//
//  private JsonNode confirmTossPaymentWithRetry(Map<String, Object> requestBody) {
//    int retryCount = 0;
//    int maxRetry = 3;
//    int retryDelayMillis = 1000;
//
//    while (retryCount < maxRetry) {
//      JsonNode response = tossWebClient.post()
//          .uri("/payments/confirm")
//          .contentType(MediaType.APPLICATION_JSON)
//          .bodyValue(requestBody)
//          .exchangeToMono(res -> res.bodyToMono(JsonNode.class))
//          .block();
//
//      if (response != null) {
//        System.out.println(">>> Toss 응답(" + (retryCount + 1) + "회차): " + response.toPrettyString());
//
//        if (response.has("code")) {
//          String errorCode = response.get("code").asText();
//          if ("ALREADY_PROCESSED_PAYMENT".equals(errorCode)) {
//            System.out.println("✅ Toss: 이미 처리된 결제. 성공으로 간주하고 종료.");
//            return response;
//          }
//        }
//
//        if (response.has("status") && "DONE".equals(response.get("status").asText())) {
//          return response;
//        }
//      }
//
//      retryCount++;
//      System.out.println("🔁 Toss 결제 재시도 " + retryCount + "회");
//
//      try {
//        Thread.sleep(retryDelayMillis);
//      } catch (InterruptedException e) {
//        Thread.currentThread().interrupt();
//        throw new RuntimeException("Toss 결제 재시도 중 인터럽트됨");
//      }
//    }
//
//    throw new RuntimeException("Toss 결제 실패 또는 최대 재시도 초과");
//  }
//}
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
import java.util.Optional;

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
  public PaymentResponseDto reserveAfterPayment(
      Long mentorId,
      String orderId,
      String paymentKey,
      int amount,
      LocalDateTime reservationTime,
      User user
  ) {
    System.out.println("=== [예약 + 결제 통합 처리 시작] ===");
    System.out.println(">>> paymentKey: " + paymentKey);
    System.out.println(">>> orderId: " + orderId);
    System.out.println(">>> amount: " + amount);
    System.out.println(">>> reserTime: " + reservationTime);

    // ✅ Step 1. 이미 처리된 결제 정보가 있는 경우: 그대로 반환 (결제 키 기준)
    Optional<Payment> existing = paymentRepository.findByPaymentKey(paymentKey);
    if (existing.isPresent()) {
      System.out.println("✅ 이미 처리된 결제 요청입니다. 예약 중복 저장 방지.");
      Payment payment = existing.get();
      MentoringReservation reservation = payment.getReservation();

      return PaymentResponseDto.builder()
          .payId(payment.getPayId())
          .amount(payment.getAmount())
          .status(payment.getStatus().name())
          .reservationStatus(reservation.getStatus().name())
          .approvedAt(payment.getApprovedAt())
          .reservationTime(reservation.getReservationTime())
          .mentorNickname(reservation.getMentor().getUser().getNickname())
          .menteeNickname(reservation.getUser().getNickname())
          .reserveId(reservation.getReserveId())
          .build();
    }

    // ✅ Step 2. Toss 결제 승인
    Map<String, Object> requestBody = Map.of(
        "paymentKey", paymentKey,
        "orderId", orderId,
        "amount", amount
    );
    JsonNode tossResponse = confirmTossPaymentWithRetry(requestBody);

    // ✅ Step 3. 결제는 성공했지만 예약이 이미 있는 경우 → 기존 예약/결제 조회해서 반환
    Optional<MentoringReservation> existingReservation =
        reservationRepository.findByMentorIdAndReservationTime(mentorId, reservationTime);

    if (existingReservation.isPresent()) {
      MentoringReservation reservation = existingReservation.get();
      Optional<Payment> existingPayment = paymentRepository.findByReservation(reservation);

      if (existingPayment.isPresent()) {
        System.out.println("✅ Toss: 예약은 이미 있음. 결제도 있으므로 그대로 응답");
        Payment payment = existingPayment.get();

        return PaymentResponseDto.builder()
            .payId(payment.getPayId())
            .amount(payment.getAmount())
            .status(payment.getStatus().name())
            .reservationStatus(reservation.getStatus().name())
            .approvedAt(payment.getApprovedAt())
            .reservationTime(reservation.getReservationTime())
            .mentorNickname(reservation.getMentor().getUser().getNickname())
            .menteeNickname(reservation.getUser().getNickname())
            .reserveId(reservation.getReserveId())
            .build();
      } else {
        // 예약은 있는데 결제가 없으면 이상 상태 (예외로 처리)
        throw new IllegalStateException("예약은 존재하지만 결제 정보가 없습니다.");
      }
    }

    // ✅ Step 4. 새로 예약 및 결제 저장
    MentorUser mentor = mentorUserRepository.findById(mentorId)
        .orElseThrow(() -> new IllegalArgumentException("멘토 정보를 찾을 수 없습니다."));

    MentoringReservation reservation = MentoringReservation.builder()
        .mentor(mentor)
        .user(user)
        .reservationTime(reservationTime)
        .status(ReservationStatus.WAITING)
        .build();
    reservationRepository.save(reservation);

    Payment payment = Payment.builder()
        .payId(orderId)
        .paymentKey(paymentKey)
        .amount(amount)
        .approvedAt(LocalDateTime.now())
        .reservation(reservation)
        .status(PaymentStatus.PAID)
        .build();
    paymentRepository.save(payment);

    // ✅ Step 5. 응답 DTO 생성
    return PaymentResponseDto.builder()
        .payId(payment.getPayId())
        .amount(payment.getAmount())
        .status(payment.getStatus().name())
        .reservationStatus(reservation.getStatus().name())
        .approvedAt(payment.getApprovedAt())
        .reservationTime(reservation.getReservationTime())
        .mentorNickname(mentor.getUser().getNickname())
        .menteeNickname(user.getNickname())
        .reserveId(reservation.getReserveId())
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
