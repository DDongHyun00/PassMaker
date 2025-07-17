package org.example.backend.admin.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.backend.inquiry.domain.Inquiry;
import org.example.backend.inquiry.domain.InquiryStatus;
import org.example.backend.inquiry.domain.InquiryType;

import static org.example.backend.inquiry.domain.InquiryType.Mentoring;

@Getter
@Builder
@AllArgsConstructor
public class AdminInquiryDetailDto {
    private int id;
    private String inquirer;
    private String title;
    private String content;
    private String type;
    private String date;
    private String status;
    private String respondTitle;
    private String respondContent;

    public static AdminInquiryDetailDto from(Inquiry inquiry) {
        return AdminInquiryDetailDto.builder()
                .id(inquiry.getId())
                .inquirer(inquiry.getUser().getName())
                .title(inquiry.getInquiryTitle())
                .content(inquiry.getInquiryContent())
                .type(translateType(inquiry.getInquiryType()))
                .status(translateStatus(inquiry.getInquiryStatus()))
                .date(inquiry.getCreatedAt().toLocalDate().toString())
                .respondTitle(inquiry.getRespondTitle() == null ? "" : inquiry.getRespondTitle())
                .respondContent(inquiry.getRespondContent() == null ? "" : inquiry.getRespondContent())
                .build();
    }

    private static String translateType(InquiryType type) {
        return switch (type) {
            case Mentoring -> "멘토링";
            case Account -> "계정";
            case Payment -> "결제";
            case Etc -> "기타";
        };
    }

    private static String translateStatus(InquiryStatus status) {
        return switch (status) {
            case PENDING -> "대기";
            case PROCESSING -> "처리중";
            case COMPLETED -> "처리완료";
        };
    }
}