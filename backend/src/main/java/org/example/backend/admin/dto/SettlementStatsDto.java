package org.example.backend.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SettlementStatsDto {
    private long totalPayment;     // 총 결제금액
    private long mentorShare;      // 멘토 정산금액
    private long adminShare;       // 관리자 정산금액
    private long pendingAmount;    // 정산 대기금액
}
