package org.example.backend.stt.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "interview_record")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SttRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "record_id")
  private Long recordId;

  @Column(name = "stt_text", columnDefinition = "TEXT")
  private String sttText; // STT 결과 (Whisper 등으로부터 변환된 텍스트)

  @Column(name = "summary_text", columnDefinition = "TEXT")
  private String summaryText; // GPT API를 통해 요약된 텍스트

  @Column(name = "room_id", nullable = false)
  private String roomId; // 어떤 화상 채팅방(room)에서의 기록인지 식별
}
