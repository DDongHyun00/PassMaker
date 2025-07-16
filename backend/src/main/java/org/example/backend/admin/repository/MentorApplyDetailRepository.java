package org.example.backend.admin.repository;

import org.example.backend.mentor.domain.MentorApply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MentorApplyDetailRepository extends JpaRepository<MentorApply, Long> {
    // 첫 번째 쿼리 - 기본 정보 + user + applyFields
    @Query("SELECT m FROM MentorApply m " +
            "JOIN FETCH m.user " +
            "LEFT JOIN FETCH m.applyFields " +
            "LEFT JOIN FETCH m.applyCareers " +
            "WHERE m.applyId = :applyId")
    Optional<MentorApply> findWithUserAndCareers(@Param("applyId") Long applyId);

    // 두 번째 쿼리 - Certifications만 별도로 조회
    @Query("SELECT m FROM MentorApply m " +
            "LEFT JOIN FETCH m.applyCertifications " +
            "WHERE m.applyId = :applyId")
    Optional<MentorApply> findWithCertifications(@Param("applyId") Long applyId);
}
