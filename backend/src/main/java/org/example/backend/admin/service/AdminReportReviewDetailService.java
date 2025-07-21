package org.example.backend.admin.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.admin.dto.AdminReportReviewDetailDto;
import org.example.backend.admin.dto.AdminReportReviewDetailInfoDto;
import org.example.backend.admin.dto.AdminReportReviewResponseDto;
import org.example.backend.admin.repository.AdminReportReviewDetailRepository;
import org.example.backend.review.domain.ReportStatus;
import org.example.backend.review.domain.ReviewReport;
import org.example.backend.review.repository.ReviewReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminReportReviewDetailService {
    private final AdminReportReviewDetailRepository adminReportReviewDetailRepository;

    public AdminReportReviewResponseDto getReportedReviewDetail(Long reportReviewId) {
        // 1. DB에서 신고된 리뷰 엔티티 조회 (AdminReportReviewDetailRepository 사용)
        ReviewReport reviewReport = adminReportReviewDetailRepository.findByIdWithReviewAndUser(reportReviewId)
                .orElseThrow(() -> new IllegalArgumentException("신고된 리뷰를 찾을 수 없습니다. ID = " + reportReviewId));

        // 2. 리뷰 정보 DTO 생성 (ReviewReport에서 연관된 Review 엔티티를 가져옴)
        AdminReportReviewDetailDto reviewDto = AdminReportReviewDetailDto.builder()
                .reviewId(reviewReport.getReview().getId()) // 리뷰 ID
                .username(reviewReport.getReview().getReservation().getUser().getName()) // 리뷰 작성자
                .reserveId(reviewReport.getReview().getReservation().getReserveId()) // 예약 ID
                .rating(reviewReport.getReview().getRating()) // 평점
                .content(reviewReport.getReview().getContent()) // 리뷰 내용
                .createdAt(reviewReport.getReview().getCreatedAt()) // 리뷰 작성일
                .build();

        // 3. 신고 정보 DTO 생성 (ReviewReport에서 신고자 정보 가져옴)
        AdminReportReviewDetailInfoDto reportDto = AdminReportReviewDetailInfoDto.builder()
                .reporterName(reviewReport.getReporter().getName()) // 신고자 이름
                .reportedAt(reviewReport.getCreatedAt()) // 신고된 시점
                .detail(reviewReport.getDetail()) // 신고 사유
                .build();

        // 4. 최종 통합 DTO 반환
        return AdminReportReviewResponseDto.builder()
                .review(reviewDto)
                .report(reportDto)
                .build();
    }

    @Transactional
    public void updateReportStatus(Long reportReviewId, String status, String rejectionReason) {
        ReviewReport report = adminReportReviewDetailRepository.findById(reportReviewId)
                .orElseThrow(() -> new IllegalArgumentException("신고 ID에 해당하는 항목을 찾을 수 없습니다."));

        ReportStatus newStatus = ReportStatus.valueOf(status.toUpperCase());
        report.setStatus(newStatus);

        if (newStatus == ReportStatus.REJECTED && rejectionReason != null) {
            report.setReason(rejectionReason); // 반려 사유 저장
            report.getReview().setReported(false);
        }

        if (newStatus == ReportStatus.REVIEWED) {
            report.getReview().setReported(true);
        }
    }
}
