package org.example.backend.inquiry.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.auth.domain.CustomUserDetails;
import org.example.backend.inquiry.dto.InquiryRequestDto;
import org.example.backend.inquiry.dto.InquiryResponseDto;
import org.example.backend.inquiry.dto.InquirySummaryDto;
import org.example.backend.inquiry.service.InquiryService;
import org.example.backend.user.domain.User;
import org.example.backend.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inquiry")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<Void> createInquiry(@AuthenticationPrincipal CustomUserDetails userDetails,
                                              @RequestBody InquiryRequestDto dto) {
        User user = userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유저 정보가 없습니다."));
        inquiryService.saveInquiry(user, dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<InquirySummaryDto>> getMyInquiries(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유저 정보가 없습니다."));
        return ResponseEntity.ok(inquiryService.getMyInquiries(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InquiryResponseDto> getInquiryDetail(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                               @PathVariable Long id) {
        User user = userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유저 정보가 없습니다."));
        return ResponseEntity.ok(inquiryService.getInquiryDetail(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInquiry(@AuthenticationPrincipal CustomUserDetails userDetails,
                                              @PathVariable Long id) {
        User user = userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유저 정보가 없습니다."));
        inquiryService.deleteInquiry(id, user);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateInquiry(@AuthenticationPrincipal CustomUserDetails userDetails,
                                              @PathVariable Long id,
                                              @RequestBody InquiryRequestDto dto) {
        User user = userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유저 정보가 없습니다."));
        inquiryService.updateInquiry(id, user, dto);
        return ResponseEntity.ok().build();
    }

}
