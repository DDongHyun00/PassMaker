package org.example.backend.admin.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.backend.admin.repository.MentorApplyDetailRepository;
import org.example.backend.mentor.domain.MentorApply;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminMentorApplyService {

    private final MentorApplyDetailRepository mentorApplyDetailRepository;

    public MentorApply getMentorApplyDetail(Long applyId) {
        MentorApply apply = mentorApplyDetailRepository.findWithUserAndCareers(applyId)
                .orElseThrow(() -> new EntityNotFoundException("신청 내역이 존재하지 않습니다."));

        // 자격증 로딩 (영속성 컨텍스트 내에서 Lazy 로딩 + fetch join)
        mentorApplyDetailRepository.findWithCertifications(applyId);

        return apply; // applyCertifications도 로딩됨
    }
}
