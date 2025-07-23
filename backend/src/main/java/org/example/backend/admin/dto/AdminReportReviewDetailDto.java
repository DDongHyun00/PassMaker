package org.example.backend.admin.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminReportReviewDetailDto {
    private Long reviewId;
    private String username;
    private Long reserveId;
    private int rating;
    private String content;
    private LocalDateTime createdAt;
}
