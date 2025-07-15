package org.example.backend.review.repository;

import org.example.backend.review.dto.ReviewReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewReportRepository extends JpaRepository<ReviewReport, Long> {
    // 필요에 따라 커스텀 조회 메서드 추가 가능
}