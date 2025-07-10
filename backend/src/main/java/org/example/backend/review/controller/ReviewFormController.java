package org.example.backend.review.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReviewFormController {

    @GetMapping("/review-form")
    public String showReviewForm() {
        return "review_form"; // src/main/resources/templates/review_form.html을 찾습니다.
    }
}
