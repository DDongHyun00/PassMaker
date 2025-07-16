package org.example.backend.auth.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.backend.auth.dto.LoginResponseDto;
import org.example.backend.config.jwt.JwtTokenProvider;
import org.example.backend.auth.domain.Refresh;
import org.example.backend.auth.domain.TokenBlacklist;
import org.example.backend.user.domain.Role;
import org.example.backend.user.domain.Status;
import org.example.backend.user.domain.User;
import org.example.backend.auth.dto.LoginRequestDto;
import org.example.backend.auth.dto.SignupRequestDto;
import org.example.backend.auth.repository.RefreshRepository;
import org.example.backend.auth.repository.TokenBlacklistRepository;
import org.example.backend.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RefreshRepository refreshRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final TokenBlacklistRepository tokenBlacklistRepository;


    // 회원가입
    @Override
    @Transactional
    public void signup(SignupRequestDto requestDto) {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        User user = User.builder()
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .nickname(requestDto.getNickname())
                .name(requestDto.getName())
                .phone(requestDto.getPhone())
                .role(Role.USER)
                .status(Status.ACTIVE)
                .build();

        userRepository.save(user);
    }

    // 로그인
    @Override
    @Transactional
    public LoginResponseDto login(LoginRequestDto requestDto, HttpServletResponse response) {
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        else if(user.getStatus() == Status.DELETED){
            throw new IllegalStateException("탈퇴한 회원입니다. 로그인이 불가능합니다.");
        }
        else if(user.getStatus() == Status.SUSPENDED){
            throw new IllegalStateException("정지된 계정입니다. 관리자에게 문의하세요.");
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        // Refresh 토큰 저장
        refreshRepository.findByUser(user).ifPresentOrElse(
                refresh -> {
                    refresh.setToken(refreshToken);
                    refreshRepository.save(refresh);
                },
                () -> {
                    Refresh refresh = Refresh.builder()
                            .user(user)
                            .token(refreshToken)
                            .build();
                    refreshRepository.save(refresh);
                }
        );

        // 쿠키에 토큰 저장
        addTokenToCookie("AccessToken", accessToken, response);
        addTokenToCookie("RefreshToken", refreshToken, response);

        // 여기서 응답 DTO 생성해서 반환
        return new LoginResponseDto(
                user.getId(),
                user.getNickname(),
                user.getRole().name()
        );
    }


    // 쿠키 생성 메서드
    private void addTokenToCookie(String name, String token, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // HTTPS 환경에서만 전송
        cookie.setPath("/");    // 쿠키의 경로 지정
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7일
//        response.addHeader("Set-Cookie", String.format("%s=%s; Path=/; HttpOnly", name, token));
        response.addCookie(cookie);
//        String cookieValue = String.format("%s=%s; Path=/; HttpOnly; Secure; SameSite=None", name, token);
//        response.addHeader("Set-Cookie", cookieValue);
    }


    @Override
    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        // 쿠키에서 refreshToken을 꺼내기
        String refreshToken = Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[]{}))
                .filter(cookie -> cookie.getName().equals("RefreshToken"))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);

        if(refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
            Long userId = jwtTokenProvider.getUserId(refreshToken);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

            // DB에서 RefreshToken 삭제
            refreshRepository.deleteByUser(user);
        }
        // AccessToken 블랙리스트 등록
        String accessToken = Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[]{}))
                .filter(cookie -> cookie.getName().equals("AccessToken"))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);

        if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
            Long userId = jwtTokenProvider.getUserId(accessToken);
            LocalDateTime expiration = jwtTokenProvider.getExpiration(accessToken)
                    .toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

            TokenBlacklist blacklist = TokenBlacklist.builder()
                    .token(accessToken)
                    .expiredAt(expiration)
                    .userId(userId)
                    .build();

            tokenBlacklistRepository.save(blacklist);
        }

        // 쿠키 삭제 (Access, Refresh 둘 다 삭제)
        expireCookie("AccessToken",response);
        expireCookie("RefreshToken",response);
    }

    // 쿠키 만료 처리 메서드
    private void expireCookie(String name, HttpServletResponse response) {
        Cookie cookie = new Cookie(name,null);
        cookie.setPath("/");  // 쿠키가 있던 원래 경로 그대로 설정
        cookie.setMaxAge(0);  // 브라우저에 "이 쿠키 바로 삭제해"라고 명령 (-1은 세션 쿠키, >0은 몇 초 후 만료, 0은 즉시 삭제)
        cookie.setHttpOnly(true); // 쿠키 생성때 HttpOnly가 되어있었다면, 같게 유지 해야 함.
        response.addCookie(cookie); // 이 쿠키를 응답에 실어서 브라우저에게 보냄
    }

    // AccessToken 재발급
    @Override
//    @Transactional
    public void reissue(HttpServletRequest request, HttpServletResponse response) {

        // 쿠키에서 RefreshToken 꺼내기
        String refreshToken = Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[]{}))
                .filter(cookie -> cookie.getName().equals("RefreshToken"))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);

        // 없으면 종료
        if(refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)){
            throw new IllegalArgumentException("RefreshToken이 유효하지 않습니다.");
        }

        // 토큰에 있는 userId 꺼냄
        Long userId = jwtTokenProvider.getUserId(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        // DB에 저장된 RefreshToken과 일치하는지 확인
        Refresh refresh = refreshRepository.findByUser(user)
                .orElseThrow(()-> new IllegalArgumentException("DB에 토큰 없음"));

        if(!refresh.getToken().equals(refreshToken)) {
            throw new IllegalArgumentException("토큰 불일치");
        }

        // 새 AccessTOken 생성 -> 쿠키로 재전송
        String newAccessToken = jwtTokenProvider.createAccessToken(user.getId());
        addTokenToCookie("AccessToken", newAccessToken, response);
    }
}
