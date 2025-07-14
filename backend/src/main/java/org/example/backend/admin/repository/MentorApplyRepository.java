package org.example.backend.admin.repository;

import org.example.backend.mentor.domain.ApplyStatus;
import org.example.backend.mentor.domain.MentorApply;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface MentorApplyRepository extends JpaRepository<MentorApply, Long>, MentorApplyRepositoryCustom {

    @EntityGraph(attributePaths = {"user", "applyFields", "applyCareers"})
    @Query("SELECT m FROM MentorApply m WHERE (:status IS NULL OR m.status = :status)")
    List<MentorApply> findWithDetailsByStatus(@Param("status") ApplyStatus status);
}
