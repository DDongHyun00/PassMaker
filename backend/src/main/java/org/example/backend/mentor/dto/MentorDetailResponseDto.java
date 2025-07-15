package org.example.backend.mentor.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentorDetailResponseDto {
    private Long mentorId;
    private String nickname;
    private String thumbnail;
    private String intro;
    private String mentoringTitle;
    private Integer hourlyRate;
    private List<String> fields; // 멘토링 분야
    private List<String> careers; // 경력
    private Double averageRating; // 평균 평점
    private Long reviewCount;     // 리뷰 개수

    // 필요한 경우 다른 정보 추가
}
