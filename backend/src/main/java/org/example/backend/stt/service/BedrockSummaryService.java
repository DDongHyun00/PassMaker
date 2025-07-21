//package org.example.backend.stt.service;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
//import software.amazon.awssdk.regions.Region;
//import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
//import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
//import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;
//import software.amazon.awssdk.core.SdkBytes;
//import java.nio.charset.StandardCharsets;
//
//@Service
//@Slf4j
//public class BedrockSummaryService {
//
//  private final BedrockRuntimeClient client;
//
//  public BedrockSummaryService() {
//    this.client = BedrockRuntimeClient.builder()
//        .region(Region.US_EAST_1) // Bedrock 지원 리전
//        .credentialsProvider(DefaultCredentialsProvider.create())
//        .build();
//  }
//
//  /**
//   * 텍스트 요약 실행
//   */
//  public String summarize(String inputText) {
//    log.info("[Bedrock 요약 시작]");
//
//    String prompt = String.format("""
//                아래는 멘토링 대화 내용입니다.
//                이를 핵심 내용 중심으로 5줄 이내로 요약해 주세요.
//
//                내용:
//                %s
//                """, inputText);
//
////    // Claude v2를 사용하는 경우 (Anthropic)
////    String payload = String.format("""
////                {
////                  "prompt": "\n\nHuman: %s\n\nAssistant:",
////                  "max_tokens_to_sample": 500,
////                  "temperature": 0.7,
////                  "top_k": 250,
////                  "top_p": 1,
////                  "stop_sequences": ["\\n\\nHuman:"]
////                }
////                """, prompt);
//
//
//    String payload = String.format("""
//  {
//    "inputText": "%s",
//    "textGenerationConfig": {
//      "maxTokenCount": 512,
//      "temperature": 0.7,
//      "topP": 1.0
//    }
//  }
//""", inputText);
//
//
//    InvokeModelRequest request = InvokeModelRequest.builder()
//        .modelId("amazon.titan-text-express-v1") // Bedrock 모델 ID "anthropic.claude-v2"이건 사전에 신청해 허가가 돼야 사용가능
//                     //or "amazon.titan-text-lite-v1" 위에나 이거 사용하면 됨
//        .contentType("application/json")
//        .accept("application/json")
//        .body(SdkBytes.fromByteBuffer(StandardCharsets.UTF_8.encode(payload)))
//        .build();
//
//    InvokeModelResponse response = client.invokeModel(request);
//    String responseBody = StandardCharsets.UTF_8.decode(response.body().asByteBuffer()).toString();
//
//
//    log.info("[Bedrock 요약 결과] {}", responseBody);
//    return extractSummary(responseBody);
//  }
//
//  /**
//   * 응답 본문에서 요약 텍스트만 추출 (가공 필요시)
//   */
//  private String extractSummary(String responseJson) {
//    // 단순 추출 예시 (실제는 JSON 파싱 라이브러리 사용하는 것이 좋음)
//    int start = responseJson.indexOf("\"completion\":\"") + 14;
//    int end = responseJson.indexOf("\"", start);
//    return responseJson.substring(start, end).replace("\\n", "\n").trim();
//  }
//}
