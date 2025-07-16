package org.example.backend.admin.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.admin.dto.AdminInquiryDetailDto;
import org.example.backend.admin.dto.AdminInquiryListDto;
import org.example.backend.inquiry.domain.Inquiry;
import org.example.backend.inquiry.domain.InquiryStatus;
import org.example.backend.inquiry.repository.InquiryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminInquiryService {
    private final InquiryRepository inquiryRepository;

    public List<AdminInquiryListDto> getAllInquiries() {
        List<Inquiry> inquiries = inquiryRepository.findAll();
        return inquiries.stream()
                .map(AdminInquiryListDto::from)
                .collect(Collectors.toList());
    }
    public AdminInquiryDetailDto getInquiryDetail(int id) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inquiry not found"));
        return AdminInquiryDetailDto.from(inquiry);
    }
    @Transactional
    public void saveResponse(int id, String respondTitle, String respondContent) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inquiry not found"));

        inquiry.setRespondTitle(respondTitle);
        inquiry.setRespondContent(respondContent);
        inquiry.setInquiryStatus(InquiryStatus.COMPLETED);
        // Dirty checking에 의해 자동으로 DB 반영됨
    }
}
