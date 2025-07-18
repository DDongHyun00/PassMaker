package org.example.backend.admin.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.admin.dto.AdminInquiryDetailDto;
import org.example.backend.admin.dto.AdminInquiryListDto;
import org.example.backend.admin.repository.AdminInquiryRepository;
import org.example.backend.inquiry.domain.Inquiry;
import org.example.backend.inquiry.domain.InquiryStatus;
import org.example.backend.inquiry.repository.InquiryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminInquiryService {
    private final AdminInquiryRepository adminInquiryRepository;

    public Page<AdminInquiryListDto> getFilteredInquiries(String searchText, String status, String type, int page, int size) {
        InquiryStatus statusEnum = null;
        if (!status.equals("전체 상태")) {
            try {
                statusEnum = InquiryStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("잘못된 상태 값입니다: " + status);
            }
        }
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Inquiry> inquiries = adminInquiryRepository.findFiltered(searchText, statusEnum, type, pageRequest);
        List<AdminInquiryListDto> dtoList = inquiries.getContent().stream()
                .map(AdminInquiryListDto::from)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageRequest, inquiries.getTotalElements());
    }

    public AdminInquiryDetailDto getInquiryDetail(int id) {
        Inquiry inquiry = adminInquiryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inquiry not found"));
        return AdminInquiryDetailDto.from(inquiry);
    }
    @Transactional
    public void saveResponse(int id, String respondTitle, String respondContent) {
        Inquiry inquiry = adminInquiryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inquiry not found"));

        inquiry.setRespondTitle(respondTitle);
        inquiry.setRespondContent(respondContent);
        inquiry.setInquiryStatus(InquiryStatus.COMPLETED);
        // Dirty checking에 의해 자동으로 DB 반영됨
    }

    public long getUnresolvedInquiryCount() {
        return adminInquiryRepository.countByInquiryStatus(InquiryStatus.PENDING);
    }
}
