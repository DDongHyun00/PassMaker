package org.example.backend.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class DashWeeklySaleDto {
    private String dayName;  // 날짜 (ex. 2025-07-17)
    private double sales;    // 매출
}
