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
import org.example.backend.config.jwt.JwtTokenProvider;
import org.example.backend.user.domain.Role;
import org.example.backend.user.domain.User;
import org.example.backend.auth.dto.Google.GoogleUserDto;
import org.example.backend.auth.repository.OAuthUserRepository;
import org.example.backend.auth.repository.RefreshRepository;
import org.example.backend.auth.repository.TokenBlacklistRepository;
import org.example.backend.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;


@RequiredArgsConstructor
@Slf4j
@Service("OAuthServiceImplGoogle")  // ★ 반드시 이름 명시!
public class OAuthServiceImplGoogle implements OAuthService {

    private final UserRepository userRepository;
    private final OAuthUserRepository oauthUserRepository;
    private final RefreshRepository refreshRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklistRepository tokenBlacklistRepository;

    @Value("${GOOGLE_USER_INFO_URL}")
    private String GOOGLE_USER_INFO_URL;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void login(String googleAccessToken, HttpServletResponse response) throws Exception {
        log.info("[Google Login] accessToken: {}", googleAccessToken);

        // 1. accessToken으로 구글 유저 정보 조회
        GoogleUserDto googleUser = getGoogleUserInfo(googleAccessToken);

        // 2. DB에 OAuthUser 존재 확인
        OAuthUser oAuthUser = oauthUserRepository
                .findBySocialIdAndSocialType(googleUser.getId(), SocialType.GOOGLE)
                .orElseGet(() -> {
                    // 2-1. 없으면 User + OAuthUser 등록
                    User newUser = User.builder()
                            .email(googleUser.getEmail())
                            .password(null)
                            .name(googleUser.getName())
                            .role(Role.USER)
                            .nickname(generateNickname(googleUser.getName()))
                            .build();
                    userRepository.save(newUser);

                    OAuthUser newOAuthUser = OAuthUser.builder()
                            .socialId(googleUser.getId())
                            .socialType(SocialType.GOOGLE)
                            .user(newUser)
                            .build();
                    return oauthUserRepository.save(newOAuthUser);
                });

        User user = oAuthUser.getUser();
        // 3. JWT 토큰 발급
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
        log.info("[Google Access/Refresj 토큰 발급 성공] : AccessToken="+accessToken + " refreshToken="+refreshToken);
        log.info("[Google Login] 성공적으로 로그인 처리됨");
    }

    private String generateNickname(String name) {  // 구글 닉네임 없을시 닉네임 생성
        return name + "_" + UUID.randomUUID().toString().substring(0, 6);
    }


    private void setTokenCookie(HttpServletResponse response, String name, String token) {
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
    // 기존에 있는 쿠키 추가 메서드 재사용
    private void addTokenToCookie(String name, String token, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60); // 1시간
        response.addCookie(cookie);
    }


    /**
     * accessToken으로 구글 사용자 정보 조회
     */
    private GoogleUserDto getGoogleUserInfo(String accessToken) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                GOOGLE_USER_INFO_URL,
                HttpMethod.GET,
                entity,
                String.class
        );

        log.info("[GoogleUserInfo] 응답: {}", response.getBody());

        JsonNode json = new ObjectMapper().readTree(response.getBody());
        return new GoogleUserDto(json);
    }

    @Override
    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        // AccessToken 블랙리스트 등록
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

    @Override
    public SocialType getSocialType() {
        return SocialType.GOOGLE;
    }
}
