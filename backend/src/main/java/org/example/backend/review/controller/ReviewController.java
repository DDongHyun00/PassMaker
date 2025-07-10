package org.example.backend.review.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.review.dto.ReviewDto;
import org.example.backend.review.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewDto.CreateResponse> createReview(@RequestBody ReviewDto.CreateRequest request) {
        ReviewDto.CreateResponse response = reviewService.createReview(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
