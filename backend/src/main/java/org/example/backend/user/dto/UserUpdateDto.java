package org.example.backend.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDto {
    private String nickname;
    private String phone;
    private String thumbnail;
    private String password; // 현재 비밀번호 확인
}
