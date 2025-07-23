package org.example.backend.admin.repository;

import org.example.backend.review.domain.ReviewReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AdminReportReviewRepository extends JpaRepository<ReviewReport, Long> {
    @Query("SELECT rr FROM ReviewReport rr " +
            "JOIN FETCH rr.review r " +
            "JOIN FETCH rr.reporter u")
    List<ReviewReport> findAllWithReviewAndUser();
}
