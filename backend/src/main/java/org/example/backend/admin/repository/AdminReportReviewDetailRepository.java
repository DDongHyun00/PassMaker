package org.example.backend.admin.repository;

import org.example.backend.review.domain.Review;
import org.example.backend.review.domain.ReviewReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AdminReportReviewDetailRepository extends JpaRepository<ReviewReport, Long> {  // ReviewReport로 변경
    @Query("SELECT rr FROM ReviewReport rr " +
            "JOIN FETCH rr.review r " +
            "JOIN FETCH r.reservation res " +
            "JOIN FETCH res.user u " +
            "WHERE rr.id = :reportReviewId")
    Optional<ReviewReport> findByIdWithReviewAndUser(@Param("reportReviewId") Long reportReviewId);

    // 신고된 리뷰를 가져오는 쿼리 (isReported 필드가 true인 경우)
    @Query("SELECT r FROM Review r " +
            "WHERE r.isReported = true")
    List<Review> findByIsReportedTrue();

    // 신고된 리뷰를 가져오는 쿼리 (ReviewReport 엔티티와 조인하는 경우)
    @Query("SELECT rr FROM ReviewReport rr " +
            "JOIN FETCH rr.review r " +
            "JOIN FETCH rr.reporter u " +
            "WHERE rr.status = 'PENDING'")
    List<ReviewReport> findPendingReports();  // 상태가 'PENDING'인 신고 리스트
}