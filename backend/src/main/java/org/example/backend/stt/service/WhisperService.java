package org.example.backend.stt.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class WhisperService implements InitializingBean {

  private final RestTemplate restTemplate = new RestTemplate();

  @Value("${openai.api.key}")
  private String openAiApiKey;

  @Override
  public void afterPropertiesSet() {
    restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
  }

  public String transcribe(MultipartFile audioFile) throws IOException {
    // 1) 파일 파트 전용 헤더 준비
    HttpHeaders filePartHeaders = new HttpHeaders();
    MediaType mediaType = MediaType.parseMediaType(
            audioFile.getContentType() != null
                    ? audioFile.getContentType()
                    : "application/octet-stream"
    );
    filePartHeaders.setContentType(mediaType);
    filePartHeaders.setContentDispositionFormData(
            "file",
            audioFile.getOriginalFilename()
    );

    // 2) 파일 리소스 래핑
    ByteArrayResource resource = new ByteArrayResource(audioFile.getBytes()) {
      @Override public String getFilename() {
        return audioFile.getOriginalFilename();
      }
      @Override public long contentLength() {
        return audioFile.getSize();
      }
    };
    HttpEntity<ByteArrayResource> filePart = new HttpEntity<>(resource, filePartHeaders);

    // 3) 나머지 폼 데이터 준비
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("file", filePart);
    body.add("model", "whisper-1");
    body.add("language", "ko");

    // ✅ prompt 추가 (대화임을 힌트로 제공)
    body.add("prompt", "이 오디오는 멘토와 멘티 간의 취업 상담 대화입니다. Whisper는 발화를 명확하게 인식해 주세요.");

    // 4) 공통 헤더 준비
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    headers.setBearerAuth(openAiApiKey);

    HttpEntity<MultiValueMap<String,Object>> request = new HttpEntity<>(body, headers);

    // 5) API 호출
    ResponseEntity<JsonNode> resp = restTemplate.exchange(
            "https://api.openai.com/v1/audio/transcriptions",
            HttpMethod.POST,
            request,
            JsonNode.class
    );

    if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
      throw new IOException("OpenAI STT API 실패: " + resp.getStatusCode());
    }

    // 6) 텍스트 추출 및 로그 출력
    String text = resp.getBody().path("text").asText();
    log.info("[OpenAI STT 결과 길이] {}자", text.length());
    log.info("[OpenAI STT 결과 내용]\n{}", text);

    // 7) 빈 값 처리
    if (text == null || text.trim().isEmpty()) {
      return "음성이 인식되지 않았습니다.";
    }

    return text;
  }
}