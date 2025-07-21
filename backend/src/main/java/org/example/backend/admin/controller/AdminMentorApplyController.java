package org.example.backend.admin.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.admin.dto.AdminMentorApplyDto;
import org.example.backend.admin.repository.AdminMentorApplyRepository;
import org.example.backend.mentor.domain.ApplyStatus;
import org.example.backend.mentor.domain.MentorApply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.backend.mentor.domain.ApplyField;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminMentorApplyController {
    private final AdminMentorApplyRepository adminMentorApplyRepository;

    @GetMapping("/mentor-application")
    public ResponseEntity<?> getMentorApplications(
            @RequestParam(defaultValue = "") String searchText,
            @RequestParam(defaultValue = "전체 상태") String status,
            @RequestParam(defaultValue = "전체 분야") String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        ApplyStatus enumStatus = null;
        if (!status.isEmpty()) {
            enumStatus = switch (status) {
                case "승인" -> ApplyStatus.APPROVED;
                case "대기" -> ApplyStatus.PENDING;
                case "거부" -> ApplyStatus.REJECTED;
                default -> null;
            };
        }

        PageRequest pageRequest = PageRequest.of(page, size,Sort.by("createdAt").descending());

        Page<MentorApply> pageResult = adminMentorApplyRepository.findFiltered(searchText, enumStatus, type, pageRequest);

        List<AdminMentorApplyDto> dtoList = pageResult.stream().map(m -> AdminMentorApplyDto.builder()
                .applyId(m.getApplyId())
                .applicationDate(m.getCreatedAt())
                .name(m.getUser().getName())
                .email(m.getUser().getEmail())
                .fields(m.getApplyFields().stream().map(ApplyField::getFieldName).toList())
                .experiences(m.getApplyCareers().stream()
                        .map(career -> career.getCompany() + " (" + career.getPeriod() + ")") // 회사와 기간을 하나의 String으로 합침
                        .collect(Collectors.toList()))
                .status(m.getStatus().name())
                .processedDate(m.getUpdatedAt())
                .build()).toList();

        return ResponseEntity.ok().body(
                Map.of(
                        "content", dtoList,
                        "totalElements", pageResult.getTotalElements(),
                        "totalPages", pageResult.getTotalPages(),
                        "page", pageResult.getNumber()
                )
        );
    }

    @GetMapping("/mentor-application/all")
    public ResponseEntity<?> getAllMentorApplications() {
        List<MentorApply> all = adminMentorApplyRepository.findAll(Sort.by("createdAt").descending());

        List<AdminMentorApplyDto> dtoList = all.stream().map(m -> AdminMentorApplyDto.builder()
                .applyId(m.getApplyId())
                .applicationDate(m.getCreatedAt())
                .name(m.getUser().getName())
                .email(m.getUser().getEmail())
                .fields(m.getApplyFields().stream().map(ApplyField::getFieldName).toList())
                .experiences(m.getApplyCareers().stream()
                        .map(career -> career.getCompany() + " (" + career.getPeriod() + ")")
                        .collect(Collectors.toList()))
                .status(m.getStatus().name())
                .processedDate(m.getUpdatedAt())
                .build()).toList();

        return ResponseEntity.ok(dtoList);
    }

}
