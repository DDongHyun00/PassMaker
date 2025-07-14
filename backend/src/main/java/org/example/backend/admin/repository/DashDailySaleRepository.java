package org.example.backend.admin.repository;

import org.example.backend.admin.dto.DashDailySaleDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DashDailySaleRepository {
    @Query("SELECT new org.example.backend.admin.dto.DashDailySaleDto(" +
            "p.payId, m.mentor.name, u.nickname, CAST(p.amount AS string)) " +
            "FROM Payment p " +
            "JOIN p.reservation r " +
            "JOIN r.mentor m " +
            "JOIN r.user u " +
            "ORDER BY p.createdAt DESC")
    List<DashDailySaleDto> findRecentReports(Pageable pageable);
}
