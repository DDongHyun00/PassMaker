package org.example.backend.admin.dto;
import lombok.Builder;
import lombok.Data;
import org.example.backend.user.domain.Status;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class UserDetailDto {
    private Long id;
    private String name;
    private String email;
    private boolean isMentor;
    private String status;
    private String statusText;
    private LocalDateTime createdAt;
    private List<ReservationInfo> reservations;

    // 상태를 변환하는 메서드
    public String convertStatus(Status status) {
        if (status == null) return "활동회원";  // 기본값
        return switch (status) {
            case ACTIVE -> "활동회원";
            case DELETED -> "탈퇴회원";
            case SUSPENDED -> "블랙리스트";
        };
    }

    // 상태 변환 후 반환
    public String getStatusText() {
        return convertStatus(Status.valueOf(this.status));
    }


    @Data
    @Builder
    public static class ReservationInfo {
        private Long reserveId;
        private LocalDateTime reservationTime;
        private String mentorName;
        private Integer amount; // 결제 금액
    }
}
