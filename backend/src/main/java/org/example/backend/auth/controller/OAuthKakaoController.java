package org.example.backend.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.external.kakao.KakaoTokenClient;
import org.example.backend.auth.service.oauth.OAuthService;
import org.example.backend.auth.service.oauth.OAuthServiceImplKakao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/*
* 인가 코드(code) → accessToken 요청 → 로그인 처리
* 프론트에서 카카오 로그인이 완료되면 아래와 같은 URL로 우리 서버에 code를 넘겨줌: GET /oauth/kakao/callback?code=abcdefg12345
* 그걸 받아서: 카카오 accessToken 요청 - accessToken으로 사용자 정보 조회 - 자동 회원가입 & JWT 발급 -쿠키에 토큰 저장
*/

@RestController
@Slf4j
public class OAuthKakaoController {

    private final OAuthServiceImplKakao oAuthServiceImplKakao;
    private final KakaoTokenClient kakaoTokenClient;
    private final @Qualifier("OAuthServiceImplKakao") OAuthService kakaoOAuthService;


    public OAuthKakaoController(
            OAuthServiceImplKakao oAuthServiceImplKakao, KakaoTokenClient kakaoTokenClient,
            @Qualifier("OAuthServiceImplKakao") OAuthService kakaoOAuthService
    ) {
        this.oAuthServiceImplKakao = oAuthServiceImplKakao;
        this.kakaoTokenClient = kakaoTokenClient;
        this.kakaoOAuthService = kakaoOAuthService;
    }

    /**
     * 카카오 로그인 콜백 URL
     * 프론트에서 인가 코드(code)를 받아서 → accessToken 요청 후 → 로그인 처리
     */

    @GetMapping("/oauth/kakao/callback")
    public void kakaoCallback(@RequestParam("code") String code, HttpServletResponse response) throws Exception {
        // 인가코드로 accessToken 요청
        String accessToken = kakaoTokenClient.requestAccessToken(code);

        // accessToken으로 사용자 정보 조희 + 자동 로그인 처리
        kakaoOAuthService.login(accessToken, response);
        log.info("카카오 로그인 성공: backend");

        response.sendRedirect("http://localhost:5173/main");
    }

    @Value("${KAKAO_CLIENT_ID}")
    private String clientId; // 또는 @Value 로 가져오기
    @Value("${KAKAO_REDIRECT_URI}")
    private String redirectUri;


    // OAuthKakaoController.java
    @GetMapping("/oauth/kakao")
    public void redirectToKakao(HttpServletResponse response) throws IOException {

        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize"
                + "?response_type=code"
                + "&client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&prompt=login";
//                + "&prompt=consent";

        response.sendRedirect(kakaoAuthUrl);
    }

    @PostMapping("/oauth/kakao/logout")
    public ResponseEntity<Void> kakaoLogout(HttpServletRequest request, HttpServletResponse response) {
        oAuthServiceImplKakao.logout(request, response);
        return ResponseEntity.ok().build();
    }
}
