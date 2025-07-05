package org.example.backend.auth.dto;

import lombok.Getter;
import org.example.backend.user.domain.User;

@Getter
public class UserResponseDto {

    private Long id;
    private String email;
    private String nickname;
    private String role;

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.role = user.getRole().name(); // enum → 문자열
    }
}
