package org.example.backend.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDto {
    private String nickname;
    private String phoneNum;
    private String profileImageUrl;
    private String currentPassword; // 현재 비밀번호 확인
}
