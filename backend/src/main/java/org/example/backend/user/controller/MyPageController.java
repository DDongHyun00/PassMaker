package org.example.backend.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.auth.domain.CustomUserDetails;
import org.example.backend.user.dto.UserProfileDto;
import org.example.backend.user.dto.UserUpdateDto;
import org.example.backend.user.service.MyPageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    // 내 정보 조회
    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getMyProfile(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long userId = customUserDetails.getUserId();
        UserProfileDto userProfile = myPageService.getUserProfile(userId);
        return ResponseEntity.ok(userProfile);
    }

    // 내 정보 수정
    @PutMapping("/profile")
    public ResponseEntity<UserProfileDto> updateUserProfile(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody UserUpdateDto userUpdateDto) {
        Long userId = customUserDetails.getUserId();
        UserProfileDto updatedUserProfile = myPageService.updateUserProfile(userId, userUpdateDto);
        return ResponseEntity.ok(updatedUserProfile);
    }
}
