package org.example.backend.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SettlementTableDto {
    private Long mentorId;
    private String mentor;
    private Integer payment;
    private Integer mentorShare;
    private Integer adminShare;
    private String status;
}
