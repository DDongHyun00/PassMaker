package org.example.backend.admin.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.admin.dto.MentorApplyDetailDto;
import org.example.backend.admin.repository.MentorApplyDetailRepository;
import org.example.backend.mentor.domain.ApplyField;
import org.example.backend.mentor.domain.MentorApply;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminMentorApplyDetailController {
    private final MentorApplyDetailRepository mentorApplyDetailRepository;

    @GetMapping("/mentor-application/{applyId}")
    public ResponseEntity<MentorApplyDetailDto> getDetail(@PathVariable Long applyId) {
        MentorApply apply = mentorApplyDetailRepository.findByIdWithUserAndCareers(applyId)
                .orElseThrow(() -> new RuntimeException("신청서를 찾을 수 없습니다."));

        List<String> applyFields = apply.getApplyFields().stream()
                .map(ApplyField::getFieldName)  // ApplyField의 fieldName을 추출
                .collect(Collectors.toList());

        MentorApplyDetailDto dto = MentorApplyDetailDto.builder()
                .applyId(apply.getApplyId())
                .applicantName(apply.getUser().getName())
                .email(apply.getUser().getEmail())
                .submittedAt(apply.getCreatedAt())
                .intro(apply.getIntro())
                .status(apply.getStatus().name())
                .reason(apply.getReason())
                .applyFields(applyFields)
                .certifications(apply.getApplyCertifications().stream()
                        .map(cert -> cert.getCertDesc()) // ApplyCertification의 certDesc를 사용
                        .toList())
                .careers(apply.getApplyCareers().stream()
                        .map(c -> MentorApplyDetailDto.CareerDto.builder()
                                .company(c.getCompany())
                                .period(c.getPeriod())
                                .build())
                        .toList())
                .build();

        return ResponseEntity.ok(dto);
    }
}
