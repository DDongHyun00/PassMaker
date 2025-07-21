package org.example.backend.admin.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminReportReviewDetailInfoDto {
    private String reporterName;
    private LocalDateTime reportedAt;
    private String detail;
}
