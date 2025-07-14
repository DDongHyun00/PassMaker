package org.example.backend.admin.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.admin.dto.UserDetailDto;
import org.example.backend.admin.repository.AdminUserDetailRepository;
import org.example.backend.user.domain.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminUserDetailController {
    private final AdminUserDetailRepository adminUserRepository;

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserDetailDto> getUserProfile(@PathVariable Long userId) {
        User user = adminUserRepository.findByIdWithReservations(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        List<UserDetailDto.ReservationInfo> reservations = user.getMentorReservations().stream()
                .map(r -> UserDetailDto.ReservationInfo.builder()
                        .reserveId(r.getReserveId())
                        .reservationTime(r.getReservationTime())
                        .mentorName(r.getMentor().getUser().getName())
                        .amount(r.getPayment() != null ? r.getPayment().getAmount() : 0)
                        .build()
                ).toList();

        return ResponseEntity.ok(
                UserDetailDto.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .isMentor(user.isMentor())
                        .createdAt(user.getCreatedAt())
                        .reservations(reservations)
                        .build()
        );
    }
}
