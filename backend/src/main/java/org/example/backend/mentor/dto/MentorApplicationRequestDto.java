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
    private List<String> fields; // ApplyField 대신 String 리스트로 변경
    private List<String> careers; // ApplyCareer 대신 String 리스트로 변경
    private List<String> certifications; // ApplyCertification 대신 String 리스트로 변경
}
