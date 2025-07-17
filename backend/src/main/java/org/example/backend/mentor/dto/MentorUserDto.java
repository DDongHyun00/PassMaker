package org.example.backend.mentor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * MPR-004: 멘토 소개글 수정 응답 DTO.
 * 업데이트된 멘토의 주요 정보를 클라이언트에 반환합니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorUserDto {
    private Long id;
    private String intro;
    private String field; // fields 리스트를 요약한 형태
    private String career; // careers 리스트를 요약한 형태
    private String cert; // certifications 리스트를 요약한 형태
    private String mentoringTitle; // 멘토링 제목
    private int hourlyRate; // 시간당 금액
    private LocalDateTime updatedAt;
}
