package org.example.backend.mentor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * MPR-006: 멘토 상태 ON/OFF 응답 DTO.
 * 멘토의 현재 활동 상태를 나타냅니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorStatusDto {
    private Long mentorId; // 멘토의 고유 ID
    private boolean isActive; // 멘토의 활동 상태 (true: 모집 중, false: 비활성)
    private LocalDateTime updatedAt; // 상태 변경 시간
}