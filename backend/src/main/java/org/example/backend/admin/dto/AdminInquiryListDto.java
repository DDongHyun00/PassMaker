package org.example.backend.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.backend.inquiry.domain.Inquiry;
import org.example.backend.inquiry.domain.InquiryStatus;
import org.example.backend.inquiry.domain.InquiryType;

@Getter
@Builder
@AllArgsConstructor
public class AdminInquiryListDto {
    private Long id;
    private String inquirer;
    private String title;
    private String content;
    private String type;    // 한글
    private String status;  // 한글
    private String date;    // 날짜만 문자열로 반환 (yyyy-MM-dd)

    public static AdminInquiryListDto from(Inquiry inquiry) {
        return AdminInquiryListDto.builder()
                .id(inquiry.getId())
                .inquirer(inquiry.getUser().getName())
                .title(inquiry.getInquiryTitle())
                .content(inquiry.getInquiryContent())
                .type(translateType(inquiry.getInquiryType()))
                .status(translateStatus(inquiry.getInquiryStatus()))
                .date(inquiry.getCreatedAt().toLocalDate().toString())  // yyyy-MM-dd
                .build();
    }

    private static String translateType(InquiryType type) {
        return switch (type) {
            case MENTORING -> "멘토링";
            case ACCOUNT -> "계정";
            case PAYMENT -> "결제";
            case ETC -> "기타";
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
