package org.example.backend.user.dto;

import lombok.Getter;

@Getter
public class ResetPasswordRequestDto {
    private String email;
    private String phone;
}
