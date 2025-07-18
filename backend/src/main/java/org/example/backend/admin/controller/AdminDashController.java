package org.example.backend.admin.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.admin.dto.DashDailySaleDto;
import org.example.backend.admin.dto.DashWeeklySaleDto;
import org.example.backend.admin.repository.AdminUserRepository;
import org.example.backend.admin.service.AdminInquiryService;
import org.example.backend.admin.service.DashDailySaleService;
import org.example.backend.admin.service.DashWeeklySaleService;
import org.example.backend.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashController {

    private final UserRepository userRepository;
    private final AdminUserRepository adminUserRepository;
    private final DashDailySaleService dashDailySaleService;
    private final DashWeeklySaleService dashWeeklySaleService;
    private final AdminInquiryService adminInquiryService;

    // 총 사용자 수 반환
    @GetMapping({"/stats"})
    public ResponseEntity<?> getAdminStats() {
        long totalUserCount = userRepository.count();
        long mentorCount = adminUserRepository.countMentors(); // isMentor = true
        long unresolvedInquiries = adminInquiryService.getUnresolvedInquiryCount(); // 미해결 문의 수

        return ResponseEntity.ok(Map.of(
                "totalUserCount", totalUserCount,
                "mentorCount", mentorCount,
                "unresolvedInquiries", unresolvedInquiries
        ));
    }

    @GetMapping("/daily")
    public ResponseEntity<List<DashDailySaleDto>> getRecentReportSummaries(
            @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(dashDailySaleService.getRecentReports(size));
    }

    @GetMapping("/weekly")
    public ResponseEntity<List<DashWeeklySaleDto>> getWeeklySales() {
        List<DashWeeklySaleDto> weeklySales = dashWeeklySaleService.getWeeklySales();
        return ResponseEntity.ok(weeklySales);
    }
}