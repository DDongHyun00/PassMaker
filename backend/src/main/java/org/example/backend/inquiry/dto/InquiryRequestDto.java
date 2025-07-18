package org.example.backend.inquiry.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.backend.inquiry.domain.InquiryType;

@Getter
@NoArgsConstructor
public class InquiryRequestDto {
    private String inquiryTitle;
    private String inquiryContent;
    private InquiryType inquiryType; // enum 그대로 사용

    @Builder
    public InquiryRequestDto(String inquiryTitle, String inquiryContent, InquiryType inquiryType) {
        this.inquiryTitle = inquiryTitle;
        this.inquiryContent = inquiryContent;
        this.inquiryType = inquiryType;
    }
}
