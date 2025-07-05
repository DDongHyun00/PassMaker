package org.example.backend.config;

import lombok.RequiredArgsConstructor;
import org.example.backend.config.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // REST API에선 CSRF 비활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/favicon.ico").permitAll()
                        .requestMatchers("/api/auth/**").permitAll() // 회원가입/로그인 허용
                        .requestMatchers("/oauth/**").permitAll()
                        .anyRequest().authenticated())               // 그 외는 인증 필요
                // .addFilterBefore(내필터, 기준이되는기존필터.class) - 기준 필터 전에 내 필터를 먼저 실행, Username~Filter는 스프링 시큐리티자체에서 로그인에 사용하는 기본필터
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
