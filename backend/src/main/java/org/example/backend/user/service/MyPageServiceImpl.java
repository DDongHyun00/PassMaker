package org.example.backend.user.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.user.domain.Status;
import org.example.backend.user.domain.User;
import org.example.backend.user.dto.UserProfileDto;
import org.example.backend.user.dto.UserUpdateDto;
import org.example.backend.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyPageServiceImpl implements MyPageService {

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
        user.setPassword(passwordEncoder.encode(userUpdateDto.getPassword()));

        User updatedUser = userRepository.save(user);

        return getUserProfile(updatedUser.getId());
    }

    // 회원 탈퇴 service
    @Transactional
    public void withdraw(User user){
        user.setStatus(Status.DELETED);
        userRepository.save(user);
    }

    @Transactional
    public void updateThumbnail(Long userId, String imageUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저 없음: id=" + userId));
        user.setThumbnail(imageUrl); // thumbnail 필드에 URL 저장
    }
}
