package org.example.backend.stt.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MentoringRecordDto {
  private Long roomId;
  private String sttText;
}
/*설명
STT 업로드 후 클라이언트에 결과 텍스트를 바로 반환합니다.
summaryText는 추후 요약 처리 후 반환 또는 조회 API에서 사용합니다.*/