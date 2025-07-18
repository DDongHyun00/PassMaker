package org.example.backend.mentor.dto;

import com.fasterxml.jackson.annotation.JsonProperty; // 이 import 추가
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * MPR-006: 멘토 상태 ON/OFF 요청 DTO.
 * 멘토의 활동 상태를 업데이트하기 위한 데이터를 담습니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorStatusUpdateDto {
    @JsonProperty("isActive") // JSON 필드 이름과 매핑
    private boolean isActive; // 변경할 멘토의 활동 상태 (true: 모집 중, false: 비활성)
}