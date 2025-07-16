package org.example.backend.payment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
public class TossWebClientConfig {

  @Value("${toss.secret-key}")
  private String secretKey;

  @Bean
  public WebClient tossWebClient() {
    String encodedKey = Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
    System.out.println("✅ Toss Auth Header = Basic " + encodedKey);

    return WebClient.builder()
        .baseUrl("https://api.tosspayments.com/v1")
        .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + encodedKey) // ✅ 자동 인증
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }
}
