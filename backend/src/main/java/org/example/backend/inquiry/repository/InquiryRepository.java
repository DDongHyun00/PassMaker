package org.example.backend.inquiry.repository;

import org.example.backend.inquiry.domain.Inquiry;
import org.example.backend.inquiry.domain.InquiryStatus;
import org.example.backend.user.domain.User;
import org.example.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

import java.util.List;
import java.util.Optional;
@Repository
public interface InquiryRepository extends JpaRepository<Inquiry,Integer> {
    // 로그인한 유저의 문의글 전체 목록 조회
    List<Inquiry> findByUser(User user);
    // 특정 문의글이 해당 유저의 것인지 검증 및 상세 조회
    Optional<Inquiry> findByIdAndUser(Long id, User user);
}
