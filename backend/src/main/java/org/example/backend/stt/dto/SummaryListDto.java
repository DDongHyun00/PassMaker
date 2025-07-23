package org.example.backend.stt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class SummaryListDto {
  private Long roomId;               // 방 ID
  private String mentoringTitle;     // 멘토 세션 제목
  private String mentorNickname;     // 멘토 닉네임
  private String menteeNickname;     // 멘티 닉네임
  private LocalDateTime summaryDate; // 요약 생성 일시
  private String status;             // 상태: completed / pending / error
  private String summaryPreview;     // 미리보기 텍스트
}
