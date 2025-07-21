package org.example.backend.admin.repository;

import org.example.backend.review.domain.ReportStatus;
import org.example.backend.review.domain.ReviewReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DashReportReviewRepository extends JpaRepository<ReviewReport, Long> {
    long countByStatus(ReportStatus status);

    List<ReviewReport> findByStatus(ReportStatus status);
}
