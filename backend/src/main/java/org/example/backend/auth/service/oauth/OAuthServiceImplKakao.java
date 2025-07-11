package org.example.backend.auth.service.oauth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.auth.domain.OAuthUser;
import org.example.backend.auth.domain.Refresh;
import org.example.backend.auth.domain.SocialType;
import org.example.backend.auth.domain.TokenBlacklist;
import org.example.backend.user.domain.Role;
import org.example.backend.user.domain.User;
import org.example.backend.auth.dto.kakao.KakaoUserDto;
import org.example.backend.auth.repository.OAuthUserRepository;
import org.example.backend.auth.repository.RefreshRepository;
import org.example.backend.auth.repository.TokenBlacklistRepository;
import org.example.backend.user.repository.UserRepository;
import org.example.backend.config.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
@Service("OAuthServiceImplKakao")
@Slf4j
public class OAuthServiceImplKakao implements OAuthService {
    private final UserRepository userRepository;
    private final OAuthUserRepository oauthUserRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshRepository refreshRepository;
    private final TokenBlacklistRepository tokenBlacklistRepository;

    @Value("${KAKAO_USER_INFO_URL}")
    private String KAKAO_USER_INFO_URL;

    // ============ 로그인 로직 (인터페이스 메서드 구현) ============ //
    /**
     * 소셜 로그인 진입점
     * - 프론트에서 받은 카카오 accessToken으로 사용자 정보 조회
     * - 우리 DB에 해당 소셜 유저가 존재하는지 확인
     * - 없으면 회원가입(User + OAuthUser)
     * - JWT 발급 → 쿠키로 전송
     */


    @Override
    public void login(String kakaoAccessToken, HttpServletResponse response) throws Exception{

        // accessToken으로 카카오 유저 정보 조회
        KakaoUserDto kakaoUser = getKakaoUserInfo(kakaoAccessToken);
        log.info("[Kakao Login] accessToken: {}", kakaoAccessToken);

        // 우리 DB에 이미 등록된 OAuthUser인지 조회
        OAuthUser oAuthUser = oauthUserRepository.findBySocialIdAndSocialType(
                kakaoUser.getId(), SocialType.KAKAO
        ).orElseGet(() -> {
            // 등록 안 된 경우 -> User + OAuthUser 자동 회원가입 처리

            // User 생성
            User newUser = User.builder()
                    .email(kakaoUser.getEmail()) // nullable
                    .password(null)              // 소셜 유저는 password 없음
                    .name(kakaoUser.getNickname()) // 임시 name으로 nickname 사용
                    .nickname(kakaoUser.getNickname())
                    .phone("010-0000-0000")      // 임시 번호 (프론트에서 수집 가능)
                    .role(Role.USER)
                    .build();

            userRepository.save(newUser);

            // OAuthUser 생성
            OAuthUser newOauthUser = OAuthUser.builder()
                    .user(newUser)
                    .socialId(kakaoUser.getId()) // 카카오 고유 ID
                    .socialType(SocialType.KAKAO)
                    .nickname(kakaoUser.getNickname())
                    .profileImageUrl(kakaoUser.getProfileImageUrl())
                    .build();

            return oauthUserRepository.save(newOauthUser);

        });

        User user = oAuthUser.getUser();
        // Access / Refresh 발급
        String accessToken = jwtTokenProvider.createAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        // Refresh 저장 (있으면 갱신, 없으면 추가)
        refreshRepository.findByUser(user).ifPresentOrElse(
                existing -> {
                    existing.setToken(refreshToken);
                    refreshRepository.save(existing);
                },
                () -> {
                    Refresh newRefresh = Refresh.builder().user(user).token(refreshToken).build();
                    refreshRepository.save(newRefresh);
                }
        );

        // 쿠키로 전달
        setTokenCookie(response, "AccessToken", accessToken);
        setTokenCookie(response, "RefreshToken", refreshToken);
        log.info("[Kakao Access/Refresj 토큰 발급 성공] : AccessToken="+accessToken + " refreshToken="+refreshToken);
        log.info("[Kakao Login] 성공적으로 로그인 처리됨");
    }


    // 어떤 소셜 플랫폼인지 구분해주는 메서드(인터페이스용)
    @Override
    public SocialType getSocialType() {
        return SocialType.KAKAO;
    }


    // access token으로 카카오 사용자 정보 요청 → DTO로 변환
    private KakaoUserDto getKakaoUserInfo(String kakaoAccessToken) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(kakaoAccessToken); // Authentication : Bearer 토큰
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED); // 이게 뭔지 모르겠음

        HttpEntity<?> entity = new HttpEntity<>(headers); // 얘도 모름

        ResponseEntity<String> response = new RestTemplate().exchange(
                KAKAO_USER_INFO_URL,
                HttpMethod.GET,
                entity,
                String.class
        );

        log.info("[KakaoUserInfo] 응답: {}", response.getBody());

        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        return new KakaoUserDto(jsonNode);
    }

    // ============ JWT 토큰 쿠키에 담기 ============ //

    /**
     * JWT 토큰을 HttpOnly + Secure 쿠키로 전달
     */

    private void setTokenCookie(HttpServletResponse response, String name, String token) {
        Cookie cookie = new Cookie(name, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60); // 1시간
        response.addCookie(cookie);
    }



    @Override
    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        // 1. AccessToken 블랙리스트 등록
        String accessToken = extractAccessTokenFromCookie(request);

        if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
            LocalDateTime expiration = jwtTokenProvider.getExpiration(accessToken)
                    .toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

            Long userId = jwtTokenProvider.getUserId(accessToken);

            TokenBlacklist blacklist = TokenBlacklist.builder()
                    .token(accessToken)
                    .expiredAt(expiration)
                    .userId(userId)
                    .build();

            tokenBlacklistRepository.save(blacklist);
        }

        String refreshToken = Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[]{}))
                .filter(cookie -> cookie.getName().equals("RefreshToken"))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);

        if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
            Long userId = jwtTokenProvider.getUserId(refreshToken);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

            // DB에서 RefreshToken 삭제
            refreshRepository.deleteByUser(user);
        }

        // 쿠키 만료
        expireCookie("AccessToken", response);
        expireCookie("RefreshToken", response);
    }
    private String extractAccessTokenFromCookie(HttpServletRequest request) {
        return Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[]{}))
                .filter(cookie -> cookie.getName().equals("AccessToken"))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    @Override
    @Transactional
    public void reissue(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[]{}))
                .filter(cookie -> cookie.getName().equals("RefreshToken"))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);

        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 RefreshToken입니다.");
        }

        Long userId = jwtTokenProvider.getUserId(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        Refresh saved = refreshRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("DB에 저장된 리프레시 토큰이 없습니다."));

        if (!saved.getToken().equals(refreshToken)) {
            throw new IllegalArgumentException("리프레시 토큰이 일치하지 않습니다.");
        }

        String newAccessToken = jwtTokenProvider.createAccessToken(userId);

        // AccessToken 재발급 → 쿠키로 전달
        addTokenToCookie("AccessToken", newAccessToken, response);
    }

    // 기존에 있는 쿠키 추가 메서드 재사용
    private void addTokenToCookie(String name, String token, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60); // 1시간
        response.addCookie(cookie);
    }

    // 쿠키 삭제용
    private void expireCookie(String name, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

}
