package org.example.backend.review.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.mentor.domain.MentorUser;
import org.example.backend.mentor.repository.MentorUserRepository;
import org.example.backend.review.domain.Review;
import org.example.backend.review.dto.ReviewActionDto;
import org.example.backend.review.dto.ReviewDto;
import org.example.backend.review.repository.ReviewRepository;
import org.example.backend.review.service.ReviewService; // 기존 ReviewService
import org.example.backend.review.service.UserReviewService; // 새로 추가된 UserReviewService
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api") // 상위 경로를 /api로 변경
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService; // 기존 ReviewService
    private final UserReviewService userReviewService; // 새로 추가된 UserReviewService
    private final ReviewRepository reviewRepository;
    private final MentorUserRepository mentorUserRepository;

    @PostMapping("/reviews") // /api/reviews
    public ResponseEntity<ReviewDto> createReview(@RequestBody ReviewActionDto request) {
        ReviewDto response = userReviewService.createReview(request); // userReviewService 호출
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 특정 멘토의 리뷰 목록을 JSON 형태로 반환하는 API 엔드포인트입니다.
     * @param mentorId 리뷰를 조회할 멘토의 ID
     * @return 특정 멘토의 리뷰 목록 (JSON)
     */
    @GetMapping("/mentors/{mentorId}/reviews") // /api/mentors/{mentorId}/reviews
    @Transactional(readOnly = true)
    public ResponseEntity<List<ReviewDto.ReviewResponse>> getReviewsByMentorId(@PathVariable Long mentorId) {
        Optional<MentorUser> mentorOptional = mentorUserRepository.findById(mentorId);
        if (mentorOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        MentorUser mentor = mentorOptional.get();
        List<Review> reviews = reviewRepository.findByMentor(mentor);

        List<ReviewDto.ReviewResponse> reviewResponses = reviews.stream()
                .map(review -> ReviewDto.ReviewResponse.builder()
                        .reviewId(review.getId())
                        .rating(review.getRating())
                        .content(review.getContent())
                        .createdAt(review.getCreatedAt())
                        .reviewer(review.getUser() != null ? ReviewDto.UserResponse.builder()
                                .id(review.getUser().getId())
                                .nickname(review.getUser().getNickname())
                                .build() : null)
                        .build())
                .collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(reviewResponses);
    }

    @GetMapping("/reviews/me") // /api/reviews/me
    public ResponseEntity<List<ReviewDto.ReviewResponse>> getReviewsByMe() {
        List<ReviewDto.ReviewResponse> myReviews = userReviewService.getReviewsByMe(); // userReviewService 호출
        return ResponseEntity.ok(myReviews);
    }
}