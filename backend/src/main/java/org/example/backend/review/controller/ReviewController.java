package org.example.backend.review.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.mentor.domain.MentorUser;
import org.example.backend.mentor.repository.MentorUserRepository;
import org.example.backend.review.domain.Review;
import org.example.backend.review.dto.ReviewDto;
import org.example.backend.review.repository.ReviewRepository;
import org.example.backend.review.service.ReviewService; // 기존 ReviewService
import org.example.backend.review.service.UserReviewService; // 새로 추가된 UserReviewService
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.example.backend.auth.domain.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.example.backend.review.dto.ReviewReportDto;

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
    public ResponseEntity<ReviewDto.CreateResponse> createReview(
            @RequestBody ReviewDto.CreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        ReviewDto.CreateResponse response = reviewService.createReview(request, userDetails.getUserId());
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
        List<Review> reviews = reviewRepository.findByReservation_Mentor(mentor);

        List<ReviewDto.ReviewResponse> reviewResponses = reviews.stream()
                .map(review -> ReviewDto.ReviewResponse.builder()
                        .reviewId(review.getId())
                        .rating(review.getRating())
                        .content(review.getContent())
                        .createdAt(review.getCreatedAt())
                        .reviewer(review.getReservation().getUser() != null ? ReviewDto.UserResponse.builder()
                                .id(review.getReservation().getUser().getId())
                                .nickname(review.getReservation().getUser().getNickname())
                                .build() : null)
                        .build())
                .collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(reviewResponses);
    }

    @GetMapping("/reviews/me") // /api/reviews/me
    public ResponseEntity<List<ReviewDto.ReviewResponse>> getReviewsByMe(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        List<ReviewDto.ReviewResponse> myReviews = userReviewService.getReviewsByMe(userId); // userReviewService 호출
        return ResponseEntity.ok(myReviews);
    }

    /**
     * MPR-007: 리뷰 신고
     * 부적절한 리뷰에 대한 신고를 처리합니다.
     * POST /api/reviews/report
     * @param reportDto 신고할 리뷰의 ID와 신고 사유를 포함하는 DTO
     * @param userDetails 현재 인증된 신고자의 정보
     * @return 성공 시 200 OK 응답
     */
    @PostMapping("/reviews/report")
    public ResponseEntity<String> reportReview(
            @RequestBody ReviewReportDto reportDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long reporterUserId = userDetails.getUserId();
        reviewService.reportReview(reportDto, reporterUserId);
        return ResponseEntity.ok("리뷰가 성공적으로 신고되었습니다.");
    }
}