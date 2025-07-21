package org.example.backend.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashDailySaleDto {
    private String payId;
    private Integer amount;
    private String userName;
    private String mentorName;
    private String date;
}
