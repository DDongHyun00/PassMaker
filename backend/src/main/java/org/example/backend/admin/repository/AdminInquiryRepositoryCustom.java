package org.example.backend.admin.repository;

import org.example.backend.inquiry.domain.Inquiry;
import org.example.backend.inquiry.domain.InquiryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminInquiryRepositoryCustom {
    Page<Inquiry> findFiltered(String searchText, InquiryStatus status, String type, Pageable pageable);
}
