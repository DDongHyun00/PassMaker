package org.example.backend.inquiry.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class InquirySummaryDto {
    private Long id;
    private String inquiryTitle;
    private LocalDateTime createdAt;
}
