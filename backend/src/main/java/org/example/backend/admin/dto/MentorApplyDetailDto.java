package org.example.backend.admin.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentorApplyDetailDto {
    private Long applyId;
    private String applicantName;
    private String email;
    private LocalDateTime submittedAt;

    private String intro; // 자기소개
    private String status; // 상태 (WAITING, APPROVED, REJECTED)
    private String reason; // 반려 사유

    private List<String> certifications;
    private List<String> applyFields;// 자격증/기술스택
    private List<CareerDto> careers;

    @Data
    @Builder
    public static class CareerDto {
        private String company;
        private String period;
    }
}
