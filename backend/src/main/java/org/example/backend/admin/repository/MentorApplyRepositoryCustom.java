package org.example.backend.admin.repository;

import org.example.backend.mentor.domain.ApplyStatus;
import org.example.backend.mentor.domain.MentorApply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MentorApplyRepositoryCustom {
    Page<MentorApply> findFiltered(String searchText, ApplyStatus status, String type, Pageable pageable);
}
