package org.example.backend.admin.repository;

import org.example.backend.admin.dto.DashDailySaleDto;
import org.example.backend.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface DashDailySaleRepository extends JpaRepository<Payment, String> {
    @Query("SELECT p FROM Payment p " +
            "JOIN FETCH p.reservation r " +
            "JOIN FETCH r.user u " +
            "JOIN FETCH r.mentor m " +
            "WHERE DATE(p.approvedAt) = CURRENT_DATE " +
            "AND p.status = 'PAID'")
    List<Payment> findTodayPaidPayments();
}