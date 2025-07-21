package org.example.backend.auth.controller;

import com.nimbusds.openid.connect.sdk.UserInfoResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.auth.domain.CustomUserDetails;
import org.example.backend.auth.dto.LoginRequestDto;
import org.example.backend.auth.dto.LoginResponseDto;
import org.example.backend.auth.dto.SignupRequestDto;
import org.example.backend.auth.dto.UserInfoResponseDto;
import org.example.backend.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequestDto requestDto) {
        authService.signup(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 성공!");
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @RequestBody LoginRequestDto requestDto,
            HttpServletResponse response) {

        LoginResponseDto loginResponse = authService.login(requestDto, response);
        return ResponseEntity.ok(loginResponse);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response){
        authService.logout(request,response);
        return ResponseEntity.ok("로그아웃 완료");
    }


    // 토큰 재발급
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response){
        authService.reissue(request,response);
        return ResponseEntity.ok("AccessToken 재발급 완료");
    }

    // 유저 토큰 확인
    @GetMapping("/me")
    public ResponseEntity<?> getMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        String username = userDetails.getUsername(); // Spring Security 기본 제공
        boolean isMentor = userDetails.getUser().isMentor(); // User 객체에서 isMentor 정보 가져오기
        return ResponseEntity.ok(new UserInfoResponseDto(userId, username, isMentor));
    }


}
