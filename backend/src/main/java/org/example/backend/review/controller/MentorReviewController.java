package org.example.backend.review.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.mentor.domain.MentorUser;
import org.example.backend.mentor.repository.MentorUserRepository;
import org.example.backend.review.domain.Review;
import org.example.backend.review.repository.ReviewRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.transaction.annotation.Transactional; // Transactional 임포트 추가

import java.util.List;
import java.util.Optional;

/**
 * 특정 멘토의 리뷰 목록 페이지를 제공하는 컨트롤러입니다.
 * Thymeleaf 템플릿을 통해 리뷰 데이터를 뷰에 전달합니다.
 */
@Controller
@RequiredArgsConstructor
public class MentorReviewController {

    private final ReviewRepository reviewRepository;
    private final MentorUserRepository mentorUserRepository;

    /**
     * 특정 멘토의 리뷰 목록을 조회하여 mentor_reviews.html 페이지를 반환합니다.
     * @param mentorId 리뷰를 조회할 멘토의 ID
     * @param model 뷰에 데이터를 전달하기 위한 Spring UI Model 객체
     * @return 멘토의 리뷰 목록을 표시하는 Thymeleaf 템플릿 이름
     */
    @GetMapping("/mentors/{mentorId}/reviews")
    @Transactional(readOnly = true) // 트랜잭션 추가
    public String getMentorReviews(@PathVariable Long mentorId, Model model) {
        Optional<MentorUser> mentorOptional = mentorUserRepository.findById(mentorId);
        if (mentorOptional.isPresent()) {
            MentorUser mentor = mentorOptional.get();
            if (mentor.getUser() != null) {
                // 멘토의 User 객체 초기화
                mentor.getUser().getNickname(); 
                model.addAttribute("mentorName", mentor.getUser().getNickname()); // 멘토 이름 (닉네임) 전달
            } else {
                model.addAttribute("mentorName", "Unknown Mentor");
            }
            List<Review> reviews = reviewRepository.findByMentor(mentor);
            // 각 리뷰의 User 객체 초기화
            reviews.forEach(review -> {
                if (review.getUser() != null) {
                    review.getUser().getNickname(); 
                }
            });
            model.addAttribute("reviews", reviews);
        } else {
            // 멘토를 찾을 수 없는 경우 처리 (예: 에러 페이지로 리다이렉트 또는 메시지 표시)
            model.addAttribute("mentorName", "Unknown Mentor");
            model.addAttribute("reviews", List.of()); // 빈 리스트 전달
        }
        return "mentor_reviews";
    }
}

