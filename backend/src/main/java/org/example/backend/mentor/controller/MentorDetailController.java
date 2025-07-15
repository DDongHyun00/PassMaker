package org.example.backend.mentor.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.mentor.domain.MentorUser;
import org.example.backend.mentor.dto.MentorDetailResponseDto;
import org.example.backend.mentor.repository.MentorUserRepository;
import org.example.backend.review.repository.ReviewRepository;
import org.example.backend.user.domain.User;
import org.example.backend.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mentors") // 멘토 관련 API는 /api/mentors 하위에 두는 것이 더 적절합니다.
@RequiredArgsConstructor
public class MentorDetailController {

    private final UserRepository userRepository;
    private final MentorUserRepository mentorUserRepository;
    private final ReviewRepository reviewRepository;

    @GetMapping("/{nickname}") // /api/mentors/{nickname}/detail
    @Transactional(readOnly = true)
    public ResponseEntity<MentorDetailResponseDto> getMentorDetailByNickname(@PathVariable String nickname) {
        Optional<User> userOptional = userRepository.findByNickname(nickname);
        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User user = userOptional.get();

        Optional<MentorUser> mentorUserOptional = mentorUserRepository.findByUser(user);
        if (mentorUserOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        MentorUser mentorUser = mentorUserOptional.get();

        // 멘토링 분야와 경력 정보 추출
        List<String> fields = mentorUser.getFields().stream()
                .map(field -> field.getFieldName())
                .collect(Collectors.toList());

        List<String> careers = mentorUser.getCareers().stream()
                .map(career -> {
                    String careerString = career.getCompany();
                    if (career.getPeriod() != null && !career.getPeriod().isEmpty()) {
                        careerString += " (" + career.getPeriod() + ")";
                    }
                    return careerString;
                })
                .collect(Collectors.toList());

        // 평균 평점 및 리뷰 개수 조회
        Double averageRating = reviewRepository.findAverageRatingByMentorId(mentorUser.getId());
        Long reviewCount = reviewRepository.countByMentorId(mentorUser.getId());

        MentorDetailResponseDto dto = MentorDetailResponseDto.builder()
                .mentorId(mentorUser.getId())
                .nickname(user.getNickname())
                .thumbnail(mentorUser.getThumbnail())
                .intro(mentorUser.getIntro())
                .mentoringTitle(mentorUser.getMentoringTitle())
                .hourlyRate(mentorUser.getHourlyRate())
                .fields(fields)
                .careers(careers)
                .averageRating(averageRating != null ? averageRating : 0.0)
                .reviewCount(reviewCount != null ? reviewCount : 0L)
                .build();

        return ResponseEntity.ok(dto);
    }
}
