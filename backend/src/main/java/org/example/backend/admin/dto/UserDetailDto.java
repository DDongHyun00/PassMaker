package org.example.backend.admin.dto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class UserDetailDto {
    private Long id;
    private String name;
    private String email;
    private boolean isMentor;
    private LocalDateTime createdAt;

    private List<ReservationInfo> reservations;

    @Data
    @Builder
    public static class ReservationInfo {
        private Long reserveId;
        private LocalDateTime reservationTime;
        private String mentorName;
        private Integer amount; // 결제 금액
    }
}
