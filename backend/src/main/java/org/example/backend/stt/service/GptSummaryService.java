package org.example.backend.stt.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GptSummaryService {

  @Value("${openai.api.key}")
  private String openAiApiKey;

  private static final String API_URL = "https://api.openai.com/v1/chat/completions";

  public String summarize(String inputText) {
    log.info("[GPT 요약 시작]");

    // GPT 요청 구성
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("model", "gpt-3.5-turbo");

    requestBody.put("messages", new Object[]{
        Map.of("role", "system", "content", "너는 멘토링 기록을 간결하게 요약하는 요약 도우미야. 핵심만 5줄 이내로 정리해줘."),
        Map.of("role", "user", "content", inputText)
    });

    requestBody.put("temperature", 0.7);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(openAiApiKey);

    HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<Map> response = restTemplate.postForEntity(API_URL, requestEntity, Map.class);

    // 응답 파싱
    Map choices = (Map) ((java.util.List) response.getBody().get("choices")).get(0);
    Map message = (Map) choices.get("message");
    String summary = (String) message.get("content");

    log.info("[GPT 요약 결과] {}", summary);
    return summary.trim();
  }
}
