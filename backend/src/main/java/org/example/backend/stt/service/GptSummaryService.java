package org.example.backend.stt.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GptSummaryService {

  @Value("${openai.api.key}")
  private String openAiApiKey;

  private static final String API_URL = "https://api.openai.com/v1/chat/completions";

  /** 요약 최대 줄 수 (실전용) */
  private static final int MAX_LINES = 20;

  public String summarize(String inputText) {
    log.info("[GPT 요약 시작] maxLines={}]", MAX_LINES);

    // 1) 요청 바디 생성
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("model", "gpt-3.5-turbo");
    requestBody.put("temperature", 0.12);
    // 대략 줄당 60토큰, 최대 토큰 수 지정
    requestBody.put("max_tokens", MAX_LINES * 60);

    // 2) system 메시지: STT 오류 보정, 스피커 구분, 분량·스타일 지시
    String systemPrompt = String.format(
        "당신은 멘토와 멘티 간의 대화를 요약하는 전문가입니다. " +
            "Whisper STT 결과에는 발음 뭉개짐이나 누락된 단어가 있을 수 있으니, 문맥을 바탕으로 보충하되, " +
            "확실하지 않으면 “［불확실］”로 표시하세요. " +
            "발언자는 “멘토:”와 “멘티:”로 반드시 구분하고, " +
            "핵심만 %d줄 이내로 알잘딱깔센 스타일—정확·간결·깔끔하게—요약해 주세요.",
        MAX_LINES
    );

    // 3) user 메시지: 실제 텍스트 + 세부 지시
    String userPrompt = String.format(
        "아래는 Whisper로 생성된 STT 텍스트입니다:\n\n%s\n\n" +
            "1) “멘토:”/“멘티:” 라벨 붙이기\n" +
            "2) 오타·뭉개진 부분 보정 (불확실 시 “［불확실］” 표시)\n" +
            "3) 핵심을 %d줄 이내로 알잘딱깔센 스타일로 간결하게 정리",
        inputText,
        MAX_LINES
    );

    requestBody.put("messages", new Object[]{
        Map.of("role", "system", "content", systemPrompt),
        Map.of("role", "user",   "content", userPrompt)
    });

    // 4) 헤더 설정
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(openAiApiKey);

    HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

    // 5) 요청 실행
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<Map> response = restTemplate.postForEntity(API_URL, requestEntity, Map.class);

    // 6) 결과 파싱
    Map choice = (Map) ((List) response.getBody().get("choices")).get(0);
    Map message = (Map) choice.get("message");
    String summary = (String) message.get("content");

    log.info("[GPT 요약 결과]\n{}", summary.trim());
    return summary.trim();
  }
}
