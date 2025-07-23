package org.example.backend.inquiry.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.backend.inquiry.domain.InquiryStatus;
import org.example.backend.inquiry.domain.InquiryType;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class InquiryResponseDto {
    private Long id;
    private String inquiryTitle;
    private String inquiryContent;
    private InquiryType inquiryType;
    private LocalDateTime createdAt;
    private String respondTitle;
    private String respondContent;
    private InquiryStatus inquiryStatus;
}
