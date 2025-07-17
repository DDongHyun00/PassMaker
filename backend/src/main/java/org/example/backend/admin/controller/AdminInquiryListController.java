package org.example.backend.admin.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.admin.dto.AdminInquiryListDto;
import org.example.backend.admin.service.AdminInquiryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminInquiryListController {
    private final AdminInquiryService adminInquiryService;

    @GetMapping("/inquiries")
    public ResponseEntity<List<AdminInquiryListDto>> getInquiries() {
        List<AdminInquiryListDto> list = adminInquiryService.getAllInquiries();
        return ResponseEntity.ok(list);
    }
}
