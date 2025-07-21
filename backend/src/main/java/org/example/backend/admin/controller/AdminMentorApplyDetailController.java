package org.example.backend.admin.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.admin.dto.AdminMentorApplyDetailDto;
import org.example.backend.admin.repository.AdminMentorApplyDetailRepository;
import org.example.backend.mentor.domain.*;
import org.example.backend.mentor.repository.MentorUserRepository;
import org.example.backend.user.domain.User;
import org.example.backend.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminMentorApplyDetailController {
    private final AdminMentorApplyDetailRepository adminMentorApplyDetailRepository;
    private final UserRepository userRepository;
    private final MentorUserRepository mentorUserRepository;

    @GetMapping("/mentor-application/{applyId}")
    public ResponseEntity<AdminMentorApplyDetailDto> getDetail(@PathVariable Long applyId) {
        MentorApply apply = adminMentorApplyDetailRepository.findWithCertifications(applyId)
                .orElseThrow(() -> new RuntimeException("신청서를 찾을 수 없습니다."));

        adminMentorApplyDetailRepository.findWithCertifications(applyId);

        List<String> applyFields = apply.getApplyFields().stream()
                .map(ApplyField::getFieldName)  // ApplyField의 fieldName을 추출
                .collect(Collectors.toList());

        AdminMentorApplyDetailDto dto = AdminMentorApplyDetailDto.builder()
                .applyId(apply.getApplyId())
                .applicantName(apply.getUser().getName())
                .email(apply.getUser().getEmail())
                .submittedAt(apply.getCreatedAt())
                .intro(apply.getIntro())
                .status(apply.getStatus().name())
                .reason(apply.getReason())
                .applyFields(applyFields)
                .certifications(apply.getApplyCertifications().stream()
                        .map(cert -> cert.getCertDesc())
                        .collect(Collectors.toList()))
                .careers(apply.getApplyCareers().stream()
                        .map(c -> AdminMentorApplyDetailDto.CareerDto.builder()
                                .company(c.getCompany())
                                .period(c.getPeriod())
                                .build())
                        .collect(Collectors.toList()))
                .build();

        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/mentor-application/{applyId}")
    @Transactional
    public ResponseEntity<String> updateApplicationStatus(
            @PathVariable Long applyId,
            @RequestParam String status,
            @RequestParam(required = false) String rejectionReason) {

        MentorApply apply = adminMentorApplyDetailRepository.findWithCertifications(applyId)
                .orElseThrow(() -> new RuntimeException("신청서를 찾을 수 없습니다."));

        ApplyStatus applyStatus;

        try {
            // 문자열을 Enum으로 변환
            applyStatus = ApplyStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status value.");
        }

        // 승인 시
        if (applyStatus == ApplyStatus.APPROVED) {
            // 신청자의 isMentor 값을 true로 업데이트
            User user = apply.getUser();
            user.setMentor(true);
            userRepository.save(user);

            // MentorUser 엔티티 생성 및 저장
            MentorUser mentorUser = MentorUser.builder()
                    .user(user)
                    .intro(apply.getIntro())
                    .mentoringTitle("제목을 여기에 설정하세요") // TODO: 기본값 또는 신청서에서 받아오기
                    .hourlyRate(0) // TODO: 기본값 설정 or 추후 설정
                    .build();

            // 지원한 분야 등록
            apply.getApplyFields().forEach(f -> {
                Field field = Field.builder()
                        .fieldName(f.getFieldName())
                        .build();
                mentorUser.addField(field);
            });

// 경력 등록
            apply.getApplyCareers().forEach(c -> {
                Career career = Career.builder()
                        .company(c.getCompany())
                        .period(c.getPeriod())
                        .build();
                mentorUser.addCareer(career);
            });

// mentorUser 저장 시 cascade로 fields, careers도 함께 저장됨
            mentorUserRepository.save(mentorUser);

            // 신청 상태 업데이트
            apply.setStatus(ApplyStatus.APPROVED);
        }
        // 반려 시
        else if (applyStatus == ApplyStatus.REJECTED) {
            apply.setStatus(ApplyStatus.REJECTED); // 상태 변경
            if (rejectionReason != null && !rejectionReason.isEmpty()) {
                apply.setReason(rejectionReason); // 반려 사유 설정
            } else {
                return ResponseEntity.badRequest().body("반려 사유를 입력해주세요.");
            }
        } else {
            return ResponseEntity.badRequest().body("Invalid status value.");
        }

        adminMentorApplyDetailRepository.save(apply); // 변경된 MentorApply 저장
        return ResponseEntity.ok("신청서 상태가 업데이트되었습니다.");
    }
}
