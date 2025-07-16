package org.example.backend.user.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.user.domain.User;
import org.example.backend.user.dto.UserProfileDto;
import org.example.backend.user.dto.UserUpdateDto;
import org.example.backend.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 내 정보 조회
    @Transactional(readOnly = true)
    public UserProfileDto getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다. id=" + userId));

        return UserProfileDto.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .name(user.getName())
                .thumbnail(user.getThumbnail())
                .isMentor(user.isMentor())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    // 내 정보 수정
    @Transactional
    public UserProfileDto updateUserProfile(Long userId, UserUpdateDto userUpdateDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다. id=" + userId));


        // 정보 업데이트
        user.setNickname(userUpdateDto.getNickname());
        user.setPhone(userUpdateDto.getPhone());
        user.setThumbnail(userUpdateDto.getThumbnail());

        User updatedUser = userRepository.save(user);

        return getUserProfile(updatedUser.getId());
    }
}
