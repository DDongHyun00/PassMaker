package org.example.backend.mentor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 멘토의 전문 분야(Field) 정보를 담는 DTO.
 * 멘토 프로필 수정 시 사용됩니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldDto {
    private String fieldName;
}
