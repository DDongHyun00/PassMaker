package org.example.backend.admin.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.admin.dto.AdminReportReviewDto;
import org.example.backend.admin.service.AdminReportReviewService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminReportReviewController {
    private final AdminReportReviewService adminReportReviewService;

    @GetMapping("/report-review")
    public List<AdminReportReviewDto> getReports(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(required = false, defaultValue = "전체 상태") String status,
            @RequestParam(required = false, defaultValue = "전체 사유") String reason
    ) {
        return adminReportReviewService.getReports(keyword, status, reason);
    }
}
