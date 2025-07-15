
package org.example.backend.mentor.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.mentor.domain.MentorUser;
import org.example.backend.mentor.dto.MentorDto;
import org.example.backend.mentor.repository.MentorRepository;
import org.example.backend.reservation.repository.ReservationRepository;
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
    private final ReservationRepository reservationRepository;   // ← 직접 주입


    // ① getAllMentors 같은 퍼블릭 서비스 메서드
    public List<MentorDto> getAllMentors() {
        return mentorRepository.findAllWithUser().stream()
                .map(this::convertToDto)   // 여기서 호출
                .collect(Collectors.toList());
    }

    // ② convertToDto 메서드: 서비스 클래스 내부에 private으로 추가
    private MentorDto convertToDto(MentorUser m) {

        if (m.getUser() == null) {
            throw new IllegalStateException("MentorUser에 연결된 User가 없습니다. id=" + m.getId());
        }

        long reviewCount = reviewRepository.countByMentorId(m.getId());
        Double avg = reviewRepository.findAverageRatingByMentorId(m.getId());
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
                .mentoringTitle(m.getMentoringTitle()) // 멘토링 제목.
                .hourlyRate(m.getHourlyRate())       // 멘토링 시간당 금액.
                .intro(m.getIntro())
                .fieldName(fields)
                .careerDesc(careers)
                .thumbnail(m.getThumbnail())
                .rating(rating)
                .reviewCount(reviewCount)
                .build();
    }
}