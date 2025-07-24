package org.example.backend.auth.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.backend.user.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        // 예: user.getRole() → USER → ROLE_USER
    }


    // TODO: 테스트용으로 추가한 메서드입니다.
    // ReservationController에서 @AuthenticationPrincipal 기반으로 userId 추출을 쉽게 하기 위해 임시 작성
    // 추후 로그인 인증 흐름과 병합 시 유지 여부 검토 필요
    public Long getUserId() { return user.getId(); }

    @Override
    public String getPassword() {
        return user.getPassword(); // 소셜 로그인 null일 수 있음
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // 또는 고유 식별자
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


}