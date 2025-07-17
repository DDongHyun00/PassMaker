package org.example.backend.user.service;

import org.example.backend.user.domain.User;
import org.example.backend.user.dto.UserProfileDto;
import org.example.backend.user.dto.UserUpdateDto;

public interface MyPageService {
    // 내 정보 조회
    UserProfileDto getUserProfile(Long userId);

    // 내 정보 수정
    UserProfileDto updateUserProfile(Long userId, UserUpdateDto userUpdateDto);

    // 회원 탈퇴
    void withdraw(User user);

    // S3 썸네일 업로드
    void updateThumbnail(Long userId, String imageUrl);
}
