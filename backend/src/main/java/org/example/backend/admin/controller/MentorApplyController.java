package org.example.backend.admin.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.admin.dto.MentorApplyDetailDto;
import org.example.backend.admin.dto.MentorApplyDto;
import org.example.backend.admin.repository.MentorApplyRepository;
import org.example.backend.mentor.domain.ApplyStatus;
import org.example.backend.mentor.domain.MentorApply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.backend.mentor.domain.ApplyCareer;
import org.example.backend.mentor.domain.ApplyField;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class MentorApplyController {
    private final MentorApplyRepository mentorApplyRepository;

    @GetMapping("/mentor-application")
    public ResponseEntity<?> getMentorApplications(
            @RequestParam(defaultValue = "") String searchText,
            @RequestParam(defaultValue = "전체 상태") String status,
            @RequestParam(defaultValue = "전체 분야") String type,
            @RequestParam(defaultValue = "최신순") String sortOrder,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        ApplyStatus enumStatus = null;
        if (!status.equals("전체 상태")) {
            try {
                enumStatus = ApplyStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("잘못된 상태값입니다: " + status);
            }
        }

        // 정렬 기준
        Sort sort = switch (sortOrder) {
            case "이름순" -> Sort.by("user.name").ascending();
            case "처리일순" -> Sort.by("updatedAt").descending();
            default -> Sort.by("createdAt").descending(); // 최신순
        };

        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Page<MentorApply> pageResult = mentorApplyRepository.findFiltered(searchText, enumStatus, type, pageRequest);

        List<MentorApplyDto> dtoList = pageResult.stream().map(m -> MentorApplyDto.builder()
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

        return ResponseEntity.ok().body(dtoList);
    }

}
