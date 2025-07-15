package org.example.backend.admin.repository;

import org.example.backend.admin.dto.DashDailySaleDto;
import org.example.backend.payment.domain.Payment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DashDailySaleRepository extends JpaRepository<Payment, String> {
    @Query("SELECT new org.example.backend.admin.dto.DashDailySaleDto(" +
            "p.payId, m.user.nickname, u.nickname, CONCAT(p.amount, '원')) " +
            "FROM Payment p " +
            "JOIN p.reservation r " +
            "JOIN r.mentor m " +     // m: MentorUser
            "JOIN r.user u " +       // u: 일반 사용자
            "ORDER BY p.createdAt DESC")
    List<DashDailySaleDto> findRecentReports(Pageable pageable);

}
