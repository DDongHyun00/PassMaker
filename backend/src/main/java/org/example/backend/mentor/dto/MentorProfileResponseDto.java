package org.example.backend.mentor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentorProfileResponseDto {
    private String thumbnail;
    private String intro;
    private String mentoringTitle;
    private Integer hourlyRate;
    private List<FieldDto> fields;
    private List<CareerDto> careers;
    private List<CertificationDto> certifications;
}
