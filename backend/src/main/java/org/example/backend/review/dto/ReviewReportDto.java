package org.example.backend.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * MPR-007: 리뷰 신고 요청 DTO.
 * 부적절한 리뷰를 신고하기 위한 데이터를 담습니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewReportDto {
    private Long reviewId; // 신고할 리뷰의 고유 ID
    private String reason; // 신고 사유 (ReviewReport 엔티티의 reason 필드와 일관성 유지)
}