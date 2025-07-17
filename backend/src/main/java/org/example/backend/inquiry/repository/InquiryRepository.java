package org.example.backend.inquiry.repository;

import org.example.backend.inquiry.domain.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry,Integer> {
}
