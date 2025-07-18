package org.example.backend.inquiry.service;

import org.example.backend.inquiry.dto.InquiryRequestDto;
import org.example.backend.inquiry.dto.InquiryResponseDto;
import org.example.backend.inquiry.dto.InquirySummaryDto;
import org.example.backend.user.domain.User;

import java.util.List;

public interface InquiryService {
    void saveInquiry(User user, InquiryRequestDto dto);
    List<InquirySummaryDto> getMyInquiries(User user);
    InquiryResponseDto getInquiryDetail(Long id, User user);
    void deleteInquiry(Long id, User user);
    void updateInquiry(Long id, User user, InquiryRequestDto dto);

}
