package org.example.backend.mentor.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.example.backend.mentor.domain.ApplyStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorApplicationResponseDto {
    private Long id;
    private Long userId;
    private String intro;
    private String mentoringTitle; // [추가] 멘토링 제목 필드
    private List<String> fields;
    private List<String> careers;
    private List<String> certifications;
    private ApplyStatus status;
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
