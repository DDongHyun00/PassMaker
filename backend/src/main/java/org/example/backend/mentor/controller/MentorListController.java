package org.example.backend.mentor.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.mentor.domain.MentorUser;
import org.example.backend.mentor.repository.MentorUserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Thymeleaf 템플릿을 통해 멘토 목록 페이지를 제공하는 컨트롤러입니다.
 * 이 컨트롤러는 백엔드 API 호출 없이 순수하게 뷰(HTML)를 반환하는 역할을 합니다.
 */
@Controller
@RequiredArgsConstructor
public class MentorListController {

    private final MentorUserRepository mentorUserRepository;

    /**
     * 멘토 목록 페이지 (mentors.html)를 반환합니다.
     * 로그인 성공 후 리다이렉트될 페이지로 사용됩니다.
     * 데이터베이스에서 멘토 목록을 조회하여 뷰에 전달합니다.
     * @param model 뷰에 데이터를 전달하기 위한 Spring UI Model 객체
     * @return 멘토 목록을 표시하는 Thymeleaf 템플릿 이름
     */
    @GetMapping("/mentors")
    public String showMentorList(Model model) {
        List<MentorUser> mentors = mentorUserRepository.findAll();
        model.addAttribute("mentors", mentors);
        return "mentors";
    }
}

