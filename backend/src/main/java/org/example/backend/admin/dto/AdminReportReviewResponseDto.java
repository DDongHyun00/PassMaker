package org.example.backend.admin.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminReportReviewResponseDto {
    private AdminReportReviewDetailDto review;
    private AdminReportReviewDetailInfoDto report;
}
