package org.example.backend.admin.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.admin.dto.AdminInquiryListDto;
import org.example.backend.admin.service.AdminInquiryService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminInquiryListController {
    private final AdminInquiryService adminInquiryService;

    @GetMapping("/inquiries")

    public ResponseEntity<?> getInquiries(
            @RequestParam(defaultValue = "") String searchText,
            @RequestParam(defaultValue = "전체 상태") String status,
            @RequestParam(defaultValue = "전체 구분") String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<AdminInquiryListDto> list = adminInquiryService.getFilteredInquiries(searchText, status, type, page, size);

        return ResponseEntity.ok(
                Map.of(
                        "content", list.getContent(),
                        "totalPages", list.getTotalPages(),
                        "totalElements", list.getTotalElements(),
                        "page", list.getNumber()
                )
        );
    }
}
