package org.example.backend.inquiry.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.inquiry.domain.Inquiry;
import org.example.backend.inquiry.domain.InquiryStatus;
import org.example.backend.inquiry.dto.InquiryRequestDto;
import org.example.backend.inquiry.dto.InquiryResponseDto;
import org.example.backend.inquiry.dto.InquirySummaryDto;
import org.example.backend.inquiry.repository.InquiryRepository;
import org.example.backend.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InquiryServiceImpl implements InquiryService {

    private final InquiryRepository inquiryRepository;

    @Override
    public void saveInquiry(User user, InquiryRequestDto dto) {
        Inquiry inquiry = Inquiry.builder()
                .user(user)
                .inquiryTitle(dto.getInquiryTitle())
                .inquiryContent(dto.getInquiryContent())
                .inquiryType(dto.getInquiryType())
                .inquiryStatus(InquiryStatus.PENDING)
                .build();

        inquiryRepository.save(inquiry);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InquirySummaryDto> getMyInquiries(User user) {
        return inquiryRepository.findByUser(user).stream()
                .map(i -> InquirySummaryDto.builder()
                        .id(i.getId())
                        .inquiryTitle(i.getInquiryTitle())
                        .createdAt(i.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public InquiryResponseDto getInquiryDetail(Long id, User user) {
        Inquiry inquiry = inquiryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("문의글을 찾을 수 없습니다."));

        return InquiryResponseDto.builder()
                .id(inquiry.getId())
                .inquiryTitle(inquiry.getInquiryTitle())
                .inquiryContent(inquiry.getInquiryContent())
                .inquiryType(inquiry.getInquiryType())
                .createdAt(inquiry.getCreatedAt())
                .respondTitle(inquiry.getRespondTitle())
                .respondContent(inquiry.getRespondContent())
                .updatedAt(inquiry.getUpdatedAt())
                .build();
    }

    @Override
    public void deleteInquiry(Long id, User user) {
        Inquiry inquiry = inquiryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 문의글이 없습니다."));
        inquiryRepository.delete(inquiry);
    }

    @Override
    public void updateInquiry(Long id, User user, InquiryRequestDto dto) {
        Inquiry inquiry = inquiryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("수정할 문의글이 없습니다."));

        inquiry.setInquiryTitle(dto.getInquiryTitle());
        inquiry.setInquiryContent(dto.getInquiryContent());
        // inquiryType도 수정 가능하게 할 경우 아래도 추가
        // inquiry.setInquiryType(dto.getInquiryType());
    }

}
