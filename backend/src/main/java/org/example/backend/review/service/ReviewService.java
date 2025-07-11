package org.example.backend.review.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.mentor.domain.MentorUser;
import org.example.backend.mentor.repository.MentorUserRepository;
import org.example.backend.review.domain.Review;
import org.example.backend.review.dto.ReviewDto;
import org.example.backend.review.repository.ReviewRepository;
import org.example.backend.user.domain.User;
import org.example.backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.example.backend.auth.domain.CustomUserDetails;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MentorUserRepository mentorUserRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReviewDto.CreateResponse createReview(ReviewDto.CreateRequest request) {
        // 현재 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated.");
        }

        // principal에서 User 객체 추출
        User user;
        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            // JwtAuthenticationFilter에서 User 객체를 직접 principal로 설정한 경우
            user = (User) principal;
        } else if (principal instanceof CustomUserDetails) {
            // JwtAuthenticationFilter에서 CustomUserDetails 객체를 principal로 설정한 경우
            user = ((CustomUserDetails) principal).getUser();
        } else {
            // 예상치 못한 principal 타입인 경우 (예외 처리)
            throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
        }

        MentorUser mentor = mentorUserRepository.findById(request.getMentorId())
                .orElseThrow(() -> new IllegalArgumentException("Mentor not found"));

        // TODO: reservationId로 예약 정보 확인 및 리뷰 작성 자격 검증 로직 추가 필요

        Review review = Review.builder()
                .mentor(mentor)
                .user(user)
                .rating(request.getRating())
                .content(request.getContent())
                .build();

        Review savedReview = reviewRepository.save(review);

        return ReviewDto.CreateResponse.builder()
                .reviewId(savedReview.getReviewId())
                .mentorId(savedReview.getMentor().getMentorId())
                .rating(savedReview.getRating())
                .content(savedReview.getContent())
                .createdAt(savedReview.getCreatedAt())
                .build();
    }
}