package org.example.backend.external.kakao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoTokenClient {

    // ========== 설정 값 주입 ========== //
    @Value("${KAKAO_CLIENT_ID}")        // application-secret.properties에서 읽어옴
    private String clientId;
    @Value("${KAKAO_REDIRECT_URI}")     // 카카오 개발자센터에서 등록한 redirect URI
    private String redirectUri;

    @Value("${KAKAO_REQUEST_URI}")
    private String TOKEN_REQUEST_URL;


    // ========== 메인 메서드: accessToken 요청 ========== //

    /**
     * 프론트에서 받은 인가 코드(code)를 가지고
     * 카카오에 access_token을 요청하는 메서드
     */

    public String requestAccessToken(String authorizationCode) throws Exception {

        System.out.println("[KakaoTokenService] clientId = " + clientId);
        System.out.println("[KakaoTokenService] redirectUri = " + redirectUri);
        System.out.println("[KakaoTokenService] code = " + authorizationCode);

        // 요청 헤더 만들기
        HttpHeaders headers = new HttpHeaders();
        // Content-Type 필수. form-urlencoded 아니면 415 오류 발생함
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 요청 바디 만들기 (x-www-form-urlencoded 형식)
        String body = "grant_type=authorization_code" +      // 고정값
                "&client_id=" + clientId +              // REST API 키
                "&redirect_uri=" + redirectUri +        // redirect URI
                "&code=" + authorizationCode;           // 프론트에서 받은 인가 코드


        // 요청 바디 + 헤더를 함께 담은 객체
        HttpEntity<String> entity = new HttpEntity<>(body,headers);

        // 카카오 서버에 요청 보내기 (POST 방식)
        ResponseEntity<String> response = new RestTemplate().exchange(
                TOKEN_REQUEST_URL,       // 카카오 토큰 발급 API
                HttpMethod.POST,
                entity,                  // 요청 본문 + 헤더
                String.class             // 응답 타입 (JSON 문자열)
        );

        log.info("카카오 응답: {}", response.getBody());

        // 응답 파싱 (JSON -> JsonNode)
        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());

        //  access_token만 필드에서 꺼내서 반환
        return jsonNode.get("access_token").asText();
    }

}
