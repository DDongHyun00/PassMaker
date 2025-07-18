package org.example.backend.admin.repository;

import org.example.backend.inquiry.domain.Inquiry;
import org.example.backend.inquiry.domain.InquiryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminInquiryRepository extends JpaRepository<Inquiry, Integer>, AdminInquiryRepositoryCustom {
    long countByInquiryStatus(InquiryStatus inquiryStatus);
}


