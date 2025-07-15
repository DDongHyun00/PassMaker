package org.example.backend.review.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewActionDto {
    private Long reservationId;
    private Long mentorId;
    private int rating;
    private String content;
}
