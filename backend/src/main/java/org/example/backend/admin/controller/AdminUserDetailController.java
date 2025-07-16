package org.example.backend.admin.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.admin.dto.AdminUserDetailDto;
import org.example.backend.admin.repository.AdminUserDetailRepository;
import org.example.backend.user.domain.Status;
import org.example.backend.user.domain.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminUserDetailController {
    private final AdminUserDetailRepository adminUserRepository;

    @GetMapping("/users/{userId}")
    public ResponseEntity<AdminUserDetailDto> getUserProfile(@PathVariable Long userId) {
        User user = adminUserRepository.findByIdWithReservations(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        List<AdminUserDetailDto.ReservationInfo> reservations = user.getMentorReservations().stream()
                .map(r -> AdminUserDetailDto.ReservationInfo.builder()
                        .reserveId(r.getReserveId())
                        .reservationTime(r.getReservationTime())
                        .mentorName(r.getMentor().getUser().getName())
                        .amount(r.getPayment() != null ? r.getPayment().getAmount() : 0)
                        .build()
                ).toList();

        return ResponseEntity.ok(
                AdminUserDetailDto.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .isMentor(user.isMentor())
                        .status(user.getStatus().name())
                        .createdAt(user.getCreatedAt())
                        .reservations(reservations)
                        .build()
        );
    }

    @PutMapping("/users/{userId}/restore")
    public ResponseEntity<String> restoreUser(@PathVariable Long userId) {
        User user = adminUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        // 유저 상태를 '활동회원'으로 변경
        user.setStatus(Status.ACTIVE);
        adminUserRepository.save(user);

        return ResponseEntity.ok("계정이 복원되었습니다.");
    }

    @PutMapping("/users/{userId}/suspend")
    public ResponseEntity<String> suspendUser(@PathVariable Long userId) {
        User user = adminUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        user.setStatus(Status.SUSPENDED);
        adminUserRepository.save(user);

        return ResponseEntity.ok("계정이 정지되었습니다.");
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        User user = adminUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        user.setStatus(Status.DELETED);
        adminUserRepository.save(user);

        return ResponseEntity.ok("계정이 삭제되었습니다.");
    }

}
