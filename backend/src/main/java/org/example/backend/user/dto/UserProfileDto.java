package org.example.backend.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class UserProfileDto {
    private Long id;
    private String nickname;
    private String email;
    private String phone;
    private String name;
    private String thumbnail;
    private boolean isMentor;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
