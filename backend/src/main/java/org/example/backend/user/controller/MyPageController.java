package org.example.backend.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.auth.domain.CustomUserDetails;
import org.example.backend.config.s3.S3Uploader;
import org.example.backend.user.dto.UserProfileDto;
import org.example.backend.user.dto.UserUpdateDto;
import org.example.backend.user.service.MyPageServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageServiceImpl myPageServiceImpl;
    private final S3Uploader s3Uploader;

    // 내 정보 조회
    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getMyProfile(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long userId = customUserDetails.getUserId();
        UserProfileDto userProfile = myPageServiceImpl.getUserProfile(userId);
        return ResponseEntity.ok(userProfile);
    }

    // 내 정보 수정
    @PatchMapping("/profile/edit")
    public ResponseEntity<UserProfileDto> updateUserProfile(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody UserUpdateDto userUpdateDto) {
        Long userId = customUserDetails.getUserId();
        UserProfileDto updatedUserProfile = myPageServiceImpl.updateUserProfile(userId, userUpdateDto);
        return ResponseEntity.ok(updatedUserProfile);
    }

    // 회원 탈퇴
    @DeleteMapping("/delete")
    public ResponseEntity<?> withdrawUser(@AuthenticationPrincipal CustomUserDetails userDetails){
        myPageServiceImpl.withdraw(userDetails.getUser());
        return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
    }

    //썸네일 업로드
    @PostMapping("/upload-thumbnail")
    public ResponseEntity<String> uploadThumbnail(
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        try {
            String imageUrl = s3Uploader.upload(file, "thumbnails"); // S3에 업로드
            myPageServiceImpl.updateThumbnail(userDetails.getUser().getId(), imageUrl); // DB 반영
            return ResponseEntity.ok(imageUrl); // 프론트에 URL 응답
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("S3 업로드 실패");
        }
    }
}
