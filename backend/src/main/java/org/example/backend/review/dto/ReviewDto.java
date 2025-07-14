package org.example.backend.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ReviewDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private Long reservationId;
        private Long Id;
        private int rating;
        private String content;
    }

    @Getter
    @Builder
    public static class CreateResponse {
        private Long reviewId;
        private Long mentorId;
        private int rating;
        private String content;
        private LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewResponse {
        private Long reviewId;
        private int rating;
        private String content;
        private LocalDateTime createdAt;
        private UserResponse reviewer;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserResponse {
        private Long id;
        private String nickname;
    }
}
