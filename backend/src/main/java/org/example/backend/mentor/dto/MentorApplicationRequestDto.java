package org.example.backend.mentor.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorApplicationRequestDto {

    private String intro;
    private String mentoringTitle; // [추가] 멘토링 제목 필드
    private List<String> fields;
    private List<CareerDto> careers; // String 리스트에서 CareerDto 리스트로 변경
    private List<String> certifications;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CareerDto {
        private String company;
        private String period;
    }
}
