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

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MentorUserRepository mentorUserRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReviewDto.CreateResponse createReview(ReviewDto.CreateRequest request) {
        // TODO: JWT 토큰에서 사용자 정보 가져와서 설정해야 함
        User user = userRepository.findById(1L) // 임시로 1번 유저 사용
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

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
