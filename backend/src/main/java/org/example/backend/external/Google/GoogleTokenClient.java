package org.example.backend.external.Google;

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
public class GoogleTokenClient {

    @Value("${GOOGLE_CLIENT_ID}")
    private String clientId;

    @Value("${GOOGLE_CLIENT_SECRET}")
    private String clientSecret;

    @Value("${GOOGLE_REDIRECT_URI}")
    private String redirectUri;

    @Value("${GOOGLE_TOKEN_URL}")
    private String tokenUrl;

    /**
     * 인가 코드로 accessToken 요청
     */
    public String requestAccessToken(String authorizationCode) throws Exception {
        log.info("GoogleTokenService 인가 코드 = {}", authorizationCode);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "grant_type=authorization_code"
                + "&client_id=" + clientId
                + "&client_secret=" + clientSecret
                + "&redirect_uri=" + redirectUri
                + "&code=" + authorizationCode;

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = new RestTemplate().exchange(
                tokenUrl,
                HttpMethod.POST,
                request,
                String.class
        );

        log.info("GoogleTokenService 응답 = {}", response.getBody());

        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        return jsonNode.get("access_token").asText();
    }
}
