package org.example.backend.user.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.user.domain.Status;
import org.example.backend.user.domain.User;
import org.example.backend.user.dto.UserProfileDto;
import org.example.backend.user.dto.UserUpdateDto;
import org.example.backend.user.repository.UserRepository;
import org.example.backend.mentor.domain.MentorUser; // [추가] MentorUser 임포트
import org.example.backend.mentor.repository.MentorUserRepository; // [추가] MentorUserRepository 임포트
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyPageServiceImpl implements MyPageService {

    private final UserRepository userRepository;
    private final MentorUserRepository mentorUserRepository; // [추가] MentorUserRepository 주입
    private final PasswordEncoder passwordEncoder;

    // 내 정보 조회
    @Transactional(readOnly = true)
    public UserProfileDto getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다. id=" + userId));

        Long mentorId = null;
        boolean isActive = false; // [추가] isActive 초기화
        if (user.isMentor()) {
            // [수정] user_id를 사용하여 MentorUser 조회
            MentorUser mentorUser = mentorUserRepository.findByUser(user)
                    .orElse(null); // 멘토가 아닐 경우 null 처리
            if (mentorUser != null) {
                mentorId = mentorUser.getId(); // MentorUser의 PK (mentorId) 가져오기
                isActive = mentorUser.isActive(); // [추가] MentorUser에서 isActive 값 가져오기
                System.out.println("DEBUG: 조회된 MentorUser의 ID (PK): " + mentorId); // [추가] 디버그 로그
            } else {
                System.out.println("DEBUG: user_id " + userId + " 에 해당하는 MentorUser를 찾을 수 없습니다."); // [추가] 디버그 로그
            }
        }

        return UserProfileDto.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .name(user.getName())
                .thumbnail(user.getThumbnail())
                .isMentor(user.isMentor())
                .mentorId(mentorId)
                .isActive(isActive) // [추가] isActive 필드 추가 // [수정] mentorId 필드 추가
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
