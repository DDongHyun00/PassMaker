
package org.example.backend.mentor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.backend.mentor.domain.MentorUser;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentorDto {
    private String nickname;
    private String intro;
    private String fieldName;
    private String careerDesc;
    private String thumbnail;
    private double rating;     // 평균 평점
    private long reviewCount;  // 리뷰 개수
}
