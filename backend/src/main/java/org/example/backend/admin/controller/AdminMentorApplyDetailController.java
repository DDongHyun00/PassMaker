package org.example.backend.admin.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.admin.dto.MentorApplyDetailDto;
import org.example.backend.admin.repository.MentorApplyDetailRepository;
import org.example.backend.mentor.domain.ApplyField;
import org.example.backend.mentor.domain.ApplyStatus;
import org.example.backend.mentor.domain.MentorApply;
import org.example.backend.user.domain.User;
import org.example.backend.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminMentorApplyDetailController {
    private final MentorApplyDetailRepository mentorApplyDetailRepository;
    private final UserRepository userRepository;

    @GetMapping("/mentor-application/{applyId}")
    public ResponseEntity<MentorApplyDetailDto> getDetail(@PathVariable Long applyId) {
        MentorApply apply = mentorApplyDetailRepository.findWithCertifications(applyId)
                .orElseThrow(() -> new RuntimeException("신청서를 찾을 수 없습니다."));

        mentorApplyDetailRepository.findWithCertifications(applyId);

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
                        .map(cert -> cert.getCertDesc())
                        .collect(Collectors.toList()))
                .careers(apply.getApplyCareers().stream()
                        .map(c -> MentorApplyDetailDto.CareerDto.builder()
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

        MentorApply apply = mentorApplyDetailRepository.findWithCertifications(applyId)
                .orElseThrow(() -> new RuntimeException("신청서를 찾을 수 없습니다."));

        mentorApplyDetailRepository.findWithCertifications(applyId);

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
            user.setMentor(true);// MentorApply에서 User를 가져옴
            userRepository.save(user); // 변경된 User 저장
            apply.setStatus(ApplyStatus.APPROVED); // 상태 변경
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

        mentorApplyDetailRepository.save(apply); // 변경된 MentorApply 저장
        return ResponseEntity.ok("신청서 상태가 업데이트되었습니다.");
    }
}
