package org.example.backend.admin.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.admin.repository.AdminUserRepository;
import org.example.backend.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminStatsController {

    private final UserRepository userRepository;
    private final AdminUserRepository adminUserRepository;

    // 총 사용자 수 반환
    @GetMapping({"", "/"})
    public ResponseEntity<?> getAdminStats() {
        long totalUserCount = userRepository.count();
        long mentorCount = adminUserRepository.countMentors(); // isMentor = true

        return ResponseEntity.ok(Map.of(
                "totalUserCount", totalUserCount,
                "mentorCount", mentorCount
        ));
    }
}