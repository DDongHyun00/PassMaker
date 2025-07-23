package org.example.backend.admin.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.admin.dto.AdminReportReviewDetailDto;
import org.example.backend.admin.dto.AdminReportReviewResponseDto;
import org.example.backend.admin.service.AdminReportReviewDetailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminReportReviewDetailController {
    private final AdminReportReviewDetailService adminReportReviewDetailService;

    @GetMapping("/report-review/{reportReviewId}")
    public Map<String, Object> getAdminReportReviewDetail(@PathVariable Long reportReviewId) {
        AdminReportReviewResponseDto responseDto = adminReportReviewDetailService.getReportedReviewDetail(reportReviewId);

        // 응답 데이터 구성
        Map<String, Object> response = new HashMap<>();
        response.put("report", responseDto); // 프론트에서 기대하는 구조: report: { ... }

        return response;
    }

    @PatchMapping("/report-review/{reportReviewId}")
    public ResponseEntity<?> updateReportReviewStatus(
            @PathVariable Long reportReviewId,
            @RequestParam("status") String status,
            @RequestParam(value = "reason", required = false) String rejectionReason
    ) {
        // 잘못된 상태 값 처리
        if (!(status.equalsIgnoreCase("APPROVED") || status.equalsIgnoreCase("REJECTED") || status.equalsIgnoreCase("REVIEWED"))) {
            return ResponseEntity.badRequest().body("잘못된 상태 값입니다.");
        }

        // "REJECTED" 상태일 경우 반려 사유가 반드시 필요
        if ("REJECTED".equalsIgnoreCase(status) && (rejectionReason == null || rejectionReason.trim().isEmpty())) {
            return ResponseEntity.badRequest().body("반려 사유를 입력해 주세요.");
        }

        // 상태에 따라 승인 처리 또는 반려 처리
        if ("REVIEWED".equalsIgnoreCase(status) || "APPROVED".equalsIgnoreCase(status)) {
            adminReportReviewDetailService.updateReportStatus(reportReviewId, status, null); // 승인 처리
        } else {
            adminReportReviewDetailService.updateReportStatus(reportReviewId, status, rejectionReason); // 반려 처리
        }

        return ResponseEntity.ok().build(); // 정상 처리
    }
}
