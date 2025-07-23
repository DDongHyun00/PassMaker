package org.example.backend.admin.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminReportReviewDto {
    private Long id;
    private String reviewId;
    private String author;
    private String content;
    private String category;
    private String status;
    private String date;
}
