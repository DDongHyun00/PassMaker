package org.example.backend.mentor.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.auth.domain.CustomUserDetails;
import org.example.backend.mentor.dto.MentorApplicationRequestDto;
import org.example.backend.mentor.dto.MentorApplicationResponseDto;
import org.example.backend.mentor.service.MentorApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mentor-applications")
@RequiredArgsConstructor
public class MentorApplicationController {

    private final MentorApplicationService mentorApplicationService;

    @PostMapping
    public ResponseEntity<MentorApplicationResponseDto> applyForMentor(
            @RequestBody MentorApplicationRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        MentorApplicationResponseDto responseDto = mentorApplicationService.applyForMentor(requestDto, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/me")
    public ResponseEntity<MentorApplicationResponseDto> getMyMentorApplicationStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        MentorApplicationResponseDto responseDto = mentorApplicationService.getMentorApplicationStatus(userDetails.getUserId());
        return ResponseEntity.ok(responseDto);
    }
}
