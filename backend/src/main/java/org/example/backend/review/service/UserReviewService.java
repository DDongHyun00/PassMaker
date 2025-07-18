package org.example.backend.review.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.review.domain.Review;
import org.example.backend.review.dto.ReviewDto;
import org.example.backend.review.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserReviewService {

    private final ReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    public List<ReviewDto.ReviewResponse> getReviewsByMe(Long userId) {
        List<Review> reviews = reviewRepository.findByReservation_User_Id(userId); // findByReservation_User_Id 메소드 필요

        return reviews.stream()
                .map(review -> ReviewDto.ReviewResponse.builder()
                        .reviewId(review.getId())
                        .rating(review.getRating())
                        .content(review.getContent())
                        .createdAt(review.getCreatedAt())
                        .reviewer(ReviewDto.UserResponse.builder()
                                .id(review.getReservation().getUser().getId())
                                .nickname(review.getReservation().getUser().getNickname())
                                .build())
                        .build()) 
                .collect(Collectors.toList());
    }
}
