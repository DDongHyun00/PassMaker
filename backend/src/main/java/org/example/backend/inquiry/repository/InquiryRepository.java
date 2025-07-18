package org.example.backend.inquiry.repository;

import org.example.backend.inquiry.domain.Inquiry;
import org.example.backend.inquiry.domain.InquiryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry,Integer> {
}
