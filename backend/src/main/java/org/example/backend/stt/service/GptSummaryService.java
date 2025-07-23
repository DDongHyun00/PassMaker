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
    requestBody.put("max_tokens", 1000);

    // 2) system 메시지: STT 오류 보정, 스피커 구분, 분량·스타일 지시
    String systemPrompt =
        "당신은 취업/커리어 멘토링 대화를 요약하는 전문가입니다. " +
            "Whisper STT 결과에는 오타, 누락, 뭉개진 표현이 있을 수 있으니 문맥을 바탕으로 자연스럽게 보정해 주세요. " +
            "불확실한 경우는 [불확실]로 표시하세요. " +
            "멘토와 멘티의 발화를 구분하여 '멘토:' / '멘티:' 라벨을 붙여주세요. " +
            "중요한 조언, 해결된 고민, 인사이트 위주로 요약하며, **장황한 표현은 피하고 깔끔하고 간결하게** 정리하세요.";


    String userPrompt = String.format(
        "다음은 STT로 인식된 멘토링 대화입니다.\n\n" +
            "[STT 내용 시작]\n%s\n[STT 내용 끝]\n\n" +
            "1) 발언자를 '멘토:', '멘티:'로 구분해 주세요.\n" +
            "2) 문맥상 어색하거나 누락된 부분은 자연스럽게 보정하세요.\n" +
            "3) 중복된 인삿말이나 잡담은 생략하고, 중요한 핵심 위주로 요약하세요.\n" +
            "4) 분량 제한은 없지만, 깔끔하고 읽기 좋게 정리하세요.",
        inputText
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
