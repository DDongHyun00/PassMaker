package org.example.backend.admin.dto;

import lombok.*;
import org.example.backend.user.domain.Status;
import org.example.backend.user.domain.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUserDto {
    private Long id;
    private String name;
    private String email;
    private String nickname;

    private String type;        // "멘토" or "멘티"
    private String status;      // "활동중" 등
    private String joinDate;    // yyyy-MM-dd

    public AdminUserDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.nickname = user.getNickname();

        this.type = user.isMentor() ? "멘토" : "멘티";
        this.status = convertStatus(user.getStatus());
        this.joinDate = user.getCreatedAt().toLocalDate().toString(); // 날짜만 추출
    }


    private String convertStatus(Status status) {
        if (status == null) return "활동회원";
        return switch (status) {
            case ACTIVE -> "활동회원";
            case DELETED -> "탈퇴회원";
            case SUSPENDED -> "블랙리스트";
        };
    }
}