package org.example.backend.admin.repository;


import org.example.backend.admin.dto.DashWeeklySaleDto;
import org.example.backend.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DashWeeklySaleRepository extends JpaRepository<Payment, String> {
    @Query("SELECT function('DATE', p.approvedAt), SUM(p.amount) " +
            "FROM Payment p " +
            "WHERE p.status = 'PAID' AND p.approvedAt BETWEEN :startOfWeek AND :endOfWeek " +
            "GROUP BY function('DATE', p.approvedAt)")
    List<Object[]> findThisWeekSales(LocalDateTime startOfWeek, LocalDateTime endOfWeek);
}
