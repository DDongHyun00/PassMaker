package org.example.backend.admin.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.admin.dto.AdminInquiryDetailDto;
import org.example.backend.admin.service.AdminInquiryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminInquiryDetailController {
    private final AdminInquiryService adminInquiryService;

    @GetMapping("/inquiries/{inquiryId}")
    public ResponseEntity<AdminInquiryDetailDto> getInquiryDetail(@PathVariable("inquiryId") int inquiryId) {
        AdminInquiryDetailDto dto = adminInquiryService.getInquiryDetail(inquiryId);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/inquiries/{inquiryId}/response")
    public ResponseEntity<Void> saveResponse(@PathVariable("inquiryId") int inquiryId, @RequestBody AdminInquiryDetailDto request) {
        adminInquiryService.saveResponse(inquiryId, request.getRespondTitle(), request.getRespondContent());
        return ResponseEntity.ok().build();
    }
}
