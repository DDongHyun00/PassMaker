package org.example.backend.mentor.service;

import org.example.backend.mentor.dto.MentorApplicationRequestDto;
import org.example.backend.mentor.dto.MentorApplicationResponseDto;

public interface MentorApplicationService {
    MentorApplicationResponseDto applyForMentor(MentorApplicationRequestDto requestDto, Long userId);
    MentorApplicationResponseDto getMentorApplicationStatus(Long userId);
}
