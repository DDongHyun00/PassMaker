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
        private Long mentorId;
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
}
