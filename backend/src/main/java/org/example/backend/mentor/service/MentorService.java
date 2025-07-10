
package org.example.backend.mentor.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.mentor.domain.MentorUser;
import org.example.backend.mentor.dto.MentorDto;
import org.example.backend.mentor.repository.MentorRepository;
import org.example.backend.review.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MentorService {

    private final MentorRepository mentorRepository;
    private final ReviewRepository reviewRepository;

    // ① getAllMentors 같은 퍼블릭 서비스 메서드
    public List<MentorDto> getAllMentors() {
        return mentorRepository.findAll().stream()
                .map(this::convertToDto)   // 여기서 호출
                .collect(Collectors.toList());
    }

    // ② convertToDto 메서드: 서비스 클래스 내부에 private으로 추가
    private MentorDto convertToDto(MentorUser m) {
        long reviewCount = reviewRepository.countByMentorId(m.getMentorId());
        Double avg = reviewRepository.findAverageRatingByMentorId(m.getMentorId());
        double rating = (avg != null)
                ? Math.round(avg * 10) / 10.0
                : 0.0;

        String fields = m.getFields().stream()
                .map(f -> f.getFieldName())
                .collect(Collectors.joining(", "));
        String careers = m.getCareers().stream()
                .map(c -> c.getCareerDesc())
                .collect(Collectors.joining(", "));

        return MentorDto.builder()
                .nickname(m.getUser().getNickname())
                .intro(m.getIntro())
                .fieldName(fields)
                .careerDesc(careers)
                .thumbnail(m.getThumbnail())
                .rating(rating)
                .reviewCount(reviewCount)
                .build();
    }
}