package org.example.backend.payment.dto;

import lombok.Getter;
import org.example.backend.payment.domain.RefundReasonType;

@Getter
public class RefundRequestDto {
  private String reasonMessage; // ✅ 프론트에서 입력한 사유 메시지
}
