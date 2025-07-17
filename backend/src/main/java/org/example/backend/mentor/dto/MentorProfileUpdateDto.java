package org.example.backend.mentor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * MPR-004: 멘토 소개글 수정 요청 DTO.
 * 멘토의 프로필 정보를 업데이트하기 위한 데이터를 담습니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorProfileUpdateDto {
    private String thumbnail;
    private String intro;
    private String mentoringTitle;
    private Integer hourlyRate;
    private List<FieldDto> fields;
    private List<CareerDto> careers;
    private List<CertificationDto> certifications;
}