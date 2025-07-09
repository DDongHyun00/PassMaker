
package org.example.backend.mentor.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.mentor.domain.MentorUser;
import org.example.backend.mentor.dto.MentorDto;
import org.example.backend.mentor.repository.MentorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MentorService {

    private final MentorRepository mentorRepository;

    public List<MentorDto> getAllMentors() {
        return mentorRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private MentorDto convertToDto(MentorUser mentorUser) {
        // Field 엔티티에서 실제 이름만 뽑아서 쉼표로 합치기
        String fieldName = mentorUser.getFields().stream()
                .map(field -> field.getFieldName())
                .collect(Collectors.joining(", "));

        // Career 엔티티에서 실제 경력 설명만 뽑아서 쉼표로 합치기
        String careerDesc = mentorUser.getCareers().stream()
                .map(career -> career.getCareerDesc())
                .collect(Collectors.joining(", "));

        return MentorDto.builder()
                .nickname(mentorUser.getUser().getName())
                .intro(mentorUser.getIntro())
                .fieldName(fieldName.isEmpty() ? null : fieldName)
                .careerDesc(careerDesc.isEmpty() ? null : careerDesc)
                .thumbnail(mentorUser.getThumbnail())
                .build();
    }
}