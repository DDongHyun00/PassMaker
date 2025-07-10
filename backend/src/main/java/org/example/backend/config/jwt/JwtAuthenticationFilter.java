package org.example.backend.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.backend.auth.domain.CustomUserDetails;
import org.example.backend.user.domain.User;
import org.example.backend.auth.repository.TokenBlacklistRepository;
import org.example.backend.user.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;  // jwt 토큰 처리 유틸
    private final UserRepository userRepository;      // 사용자 정보 조회용
    private final TokenBlacklistRepository tokenBlacklistRepository; // 토큰 블랙리스트 확인

    /* HTTP 요청이 들어올 때마다 실행되는 메서드 */

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 쿠키에서 AccessToken 꺼내기
        String token  = extractTokenFromCookie(request);

        // 토큰이 존재하고 유효하면 인증 처리
        if(token != null && jwtTokenProvider.validateToken(token)){

            // 블랙리스트 체크
            if (tokenBlacklistRepository.existsByToken(token)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그아웃된 토큰입니다.");
                return;
            }

            // 토큰에서 userId꺼냄
            Long userId = jwtTokenProvider.getUserId(token);

            // userId로 DB에서 사용자 조회
            Optional<User> userOptional = userRepository.findById(userId);
            if(userOptional.isPresent()) {
                User user = userOptional.get();
                CustomUserDetails userDetails = new CustomUserDetails(user);

                // 스프링 시큐리티 인증 객체 생성
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
//                                user,                // 인증 주체(principal)
                            userDetails,
                                null,                // 비밀번호 (null로 설정)
                                userDetails.getAuthorities()                 // 권한 정보 (ex. USER/ADMIN - 우선 null)
                        );

                // 요청 정보 세팅 (IP, 세션 등)
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

//                System.out.println("AccessToken = " + token);
//                System.out.println("유효한 토큰인가? " + jwtTokenProvider.validateToken(token));
//                System.out.println("추출된 userId = " + userId);
//                System.out.println("DB에서 조회된 사용자 있음? " + userOptional.isPresent());

                // 인증 객체를 SecurityContext에 등록 [핵심]
                SecurityContextHolder.getContext().setAuthentication(authentication);


            }
        }
        // 다음 필터로 전달 (다음필터 없으면 컨트롤러로 넘어감)
        filterChain.doFilter(request, response);
    }

    /* 요청에 포함된 쿠키 중 AccessToken만 추출하는 메서드 */
    private String extractTokenFromCookie(HttpServletRequest request){
        if (request.getCookies() == null)
            return null;

        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("AccessToken"))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }
}
