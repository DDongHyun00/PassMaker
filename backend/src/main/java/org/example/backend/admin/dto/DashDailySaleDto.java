package org.example.backend.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashDailySaleDto {
    private String id;
    private String mentor;
    private String mentee;
    private String amount;
}
