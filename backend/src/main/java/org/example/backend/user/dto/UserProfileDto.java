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
    private Long mentorId; // [추가] 멘토의 고유 ID (mentor_user 테이블의 PK)
    private boolean isActive; // [추가] 멘토 활동 상태 필드
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
