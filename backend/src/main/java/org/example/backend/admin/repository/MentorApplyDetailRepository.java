package org.example.backend.admin.repository;

import org.example.backend.mentor.domain.MentorApply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MentorApplyDetailRepository extends JpaRepository<MentorApply, Long> {
    @Query("SELECT m FROM MentorApply m " +
            "JOIN FETCH m.user " +
            "LEFT JOIN FETCH m.applyFields " + // 하나만 fetch
            "WHERE m.applyId = :applyId")
    Optional<MentorApply> findByIdWithUserAndCareers(@Param("applyId") Long applyId);
}
