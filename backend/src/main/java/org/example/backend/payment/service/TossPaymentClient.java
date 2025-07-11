package org.example.backend.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TossPaymentClient {

  private final WebClient tossWebClient;

  @Value("${toss.secret-key}")
  private String secretKey;

  public void requestRefund(String paymentKey, String cancelReason, int cancelAmount) {
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("cancelReason", cancelReason);
    requestBody.put("cancelAmount", cancelAmount);

    tossWebClient.post()
        .uri("/payments/" + paymentKey + "/cancel")
        .header(HttpHeaders.AUTHORIZATION, "Basic " + encodeSecretKey(secretKey))
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(requestBody)
        .retrieve()
        .bodyToMono(String.class)
        .doOnSuccess(response -> System.out.println("✅ Toss 환불 성공: " + response))
        .doOnError(error -> System.out.println("❌ Toss 환불 실패: " + error.getMessage()))
        .block(); // 동기 호출
  }

  private String encodeSecretKey(String secretKey) {
    return Base64.getEncoder().encodeToString((secretKey + ":").getBytes());
  }
}
