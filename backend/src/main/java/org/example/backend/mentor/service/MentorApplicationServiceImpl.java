package org.example.backend.mentor.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.mentor.domain.ApplyStatus;
import org.example.backend.mentor.domain.MentorApply;
import org.example.backend.mentor.dto.MentorApplicationRequestDto;
import org.example.backend.mentor.dto.MentorApplicationResponseDto;
import org.example.backend.mentor.repository.ApplyCareerRepository;
import org.example.backend.mentor.repository.ApplyCertificationRepository;
import org.example.backend.mentor.repository.ApplyFieldRepository;
import org.example.backend.admin.repository.MentorApplyRepository;
import org.example.backend.user.domain.User;
import org.example.backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MentorApplicationServiceImpl implements MentorApplicationService {

    private final MentorApplyRepository mentorApplyRepository;
    private final UserRepository userRepository;
    private final ApplyFieldRepository applyFieldRepository;
    private final ApplyCareerRepository applyCareerRepository;
    private final ApplyCertificationRepository applyCertificationRepository;

    @Override
    @Transactional
    public MentorApplicationResponseDto applyForMentor(MentorApplicationRequestDto requestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (mentorApplyRepository.findByUser(user).isPresent()) {
            throw new IllegalStateException("Mentor application already exists for this user.");
        }

        MentorApply mentorApply = MentorApply.builder()
                .user(user)
                .intro(requestDto.getIntro())
                .status(ApplyStatus.PENDING) // 초기 상태는 PENDING
                .build();

        MentorApply savedApply = mentorApplyRepository.save(mentorApply);

        // fields 저장
        if (requestDto.getFields() != null) {
            requestDto.getFields().forEach(fieldName -> {
                applyFieldRepository.save(new org.example.backend.mentor.domain.ApplyField(null, savedApply, fieldName));
            });
        }

        // careers 저장
        if (requestDto.getCareers() != null) {
            requestDto.getCareers().forEach(careerString -> {
                // "회사명 (기간)" 형식에서 회사명과 기간을 파싱
                String company = careerString;
                String period = null;
                int startIndex = careerString.indexOf("(");
                int endIndex = careerString.indexOf(")");
                if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                    company = careerString.substring(0, startIndex).trim();
                    period = careerString.substring(startIndex + 1, endIndex).trim();
                }
                applyCareerRepository.save(new org.example.backend.mentor.domain.ApplyCareer(null, savedApply, company, period));
            });
        }

        // certifications 저장
        if (requestDto.getCertifications() != null) {
            requestDto.getCertifications().forEach(certDesc -> {
                applyCertificationRepository.save(new org.example.backend.mentor.domain.ApplyCertification(null, savedApply, certDesc));
            });
        }

        return MentorApplicationResponseDto.builder()
                .id(savedApply.getApplyId())
                .userId(savedApply.getUser().getUserId())
                .intro(savedApply.getIntro())
                .status(savedApply.getStatus())
                .fields(requestDto.getFields())
                .careers(requestDto.getCareers())
                .certifications(requestDto.getCertifications())
                .createdAt(savedApply.getCreatedAt())
                .updatedAt(savedApply.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public MentorApplicationResponseDto getMentorApplicationStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        MentorApply mentorApply = mentorApplyRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Mentor application not found for this user."));

        // 관련 엔티티들을 DTO로 변환하여 포함
        return MentorApplicationResponseDto.builder()
                .id(mentorApply.getApplyId())
                .userId(mentorApply.getUser().getUserId())
                .intro(mentorApply.getIntro())
                .status(mentorApply.getStatus())
                .reason(mentorApply.getReason())
                .fields(mentorApply.getApplyFields().stream().map(org.example.backend.mentor.domain.ApplyField::getFieldName).collect(Collectors.toList()))
                .careers(mentorApply.getApplyCareers().stream()
                        .map(applyCareer -> {
                            String careerString = applyCareer.getCompany();
                            if (applyCareer.getPeriod() != null && !applyCareer.getPeriod().isEmpty()) {
                                careerString += " (" + applyCareer.getPeriod() + ")";
                            }
                            return careerString;
                        })
                        .collect(Collectors.<String>toList()))
                .certifications(mentorApply.getApplyCertifications().stream().map(org.example.backend.mentor.domain.ApplyCertification::getCertDesc).collect(Collectors.toList()))
                .createdAt(mentorApply.getCreatedAt())
                .updatedAt(mentorApply.getUpdatedAt())
                .build();
    }
}
