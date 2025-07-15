package org.example.backend.review.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.auth.domain.CustomUserDetails;
import org.example.backend.mentor.domain.MentorUser;
import org.example.backend.mentor.repository.MentorUserRepository;
import org.example.backend.reservation.domain.MentoringReservation;
import org.example.backend.reservation.domain.ReservationStatus;
import org.example.backend.reservation.repository.ReservationRepository;
import org.example.backend.review.domain.Review;
import org.example.backend.review.dto.ReviewActionDto;
import org.example.backend.review.dto.ReviewDto;
import org.example.backend.review.repository.ReviewRepository;
import org.example.backend.user.domain.User;
import org.example.backend.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserReviewService {

    private final ReviewRepository reviewRepository;
    private final MentorUserRepository mentorUserRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public ReviewDto createReview(ReviewActionDto request) {
        // 현재 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated.");
        }

        // principal에서 User 객체 추출
        User user;
        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            user = (User) principal;
        } else if (principal instanceof CustomUserDetails) {
            user = ((CustomUserDetails) principal).getUser();
        } else {
            throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
        }

        MentorUser mentor = mentorUserRepository.findById(request.getMentorId())
                .orElseThrow(() -> new IllegalArgumentException("Mentor not found"));

        // 예약 정보 확인 및 리뷰 작성 자격 검증
        MentoringReservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        if (!reservation.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You are not authorized to review this reservation.");
        }

        if (!reservation.getStatus().equals(ReservationStatus.ACCEPT)) {
            throw new IllegalStateException("Review can only be written for completed reservations.");
        }

        // TODO: 이미 리뷰가 작성되었는지 확인하는 로직 추가 필요

        Review review = Review.builder()
                .mentor(mentor)
                .user(user)
                .rating(request.getRating())
                .content(request.getContent())
                .reservation(reservation) // 예약 정보 추가
                .build();

        Review savedReview = reviewRepository.save(review);

        return ReviewDto.builder()
                .reviewId(savedReview.getId())
                .mentorId(savedReview.getMentor().getId())
                .rating(savedReview.getRating())
                .content(savedReview.getContent())
                .createdAt(savedReview.getCreatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ReviewDto.ReviewResponse> getReviewsByMe() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated.");
        }

        User user;
        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            user = (User) principal;
        } else if (principal instanceof CustomUserDetails) {
            user = ((CustomUserDetails) principal).getUser();
        } else {
            throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
        }

        List<Review> reviews = reviewRepository.findByUser(user); // findByUser 메소드 필요

        return reviews.stream()
                .map(review -> ReviewDto.ReviewResponse.builder()
                        .reviewId(review.getId())
                        .rating(review.getRating())
                        .content(review.getContent())
                        .createdAt(review.getCreatedAt())
                        .reviewer(ReviewDto.UserResponse.builder()
                                .id(review.getUser().getId())
                                .nickname(review.getUser().getNickname())
                                .build())
                        .build()) 
                .collect(Collectors.toList());
    }
}
