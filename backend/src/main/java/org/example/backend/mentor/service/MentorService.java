
package org.example.backend.mentor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.mentor.domain.Career;
import org.example.backend.mentor.domain.Certification;
import org.example.backend.mentor.domain.Field;
import org.example.backend.mentor.domain.MentorUser;
import org.example.backend.mentor.dto.CareerDto;
import org.example.backend.mentor.dto.CertificationDto;
import org.example.backend.mentor.dto.FieldDto;
import org.example.backend.mentor.dto.MentorDto;
import org.example.backend.mentor.dto.MentorProfileUpdateDto;
import org.example.backend.mentor.dto.MentorUserDto;
import org.example.backend.mentor.repository.CareerRepository;
import org.example.backend.mentor.repository.CertificationRepository;
import org.example.backend.mentor.repository.FieldRepository;
import org.example.backend.mentor.repository.MentorRepository;
import org.example.backend.reservation.repository.ReservationRepository;
import org.example.backend.review.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MentorService {

    private final MentorRepository mentorRepository;
    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final FieldRepository fieldRepository; // 추가
    private final CareerRepository careerRepository; // 추가
    private final CertificationRepository certificationRepository; // 추가


    // ① getAllMentors 같은 퍼블릭 서비스 메서드
    public List<MentorDto> getAllMentors() {
        List<MentorUser> mentors = mentorRepository.findAllWithUser();
        for (MentorUser m : mentors) {
            if (m.getUser() == null) {
                log.error("❌ 연결된 User 없음: mentorId = {}", m.getId());
            }
        }
        return mentorRepository.findAllWithUser().stream()
                .map(this::convertToDto)   // 여기서 호출
                .collect(Collectors.toList());
    }

    // ② convertToDto 메서드: 서비스 클래스 내부에 private으로 추가
    private MentorDto convertToDto(MentorUser m) {

        if (m.getUser() == null) {
            throw new IllegalStateException("MentorUser에 연결된 User가 없습니다. id=" + m.getId());
        }

        long reviewCount = reviewRepository.countByMentorId(m.getId());
        Double avg = reviewRepository.findAverageRatingByMentorId(m.getId());
        double rating = (avg != null)
                ? Math.round(avg * 10) / 10.0
                : 0.0;

        String fields = m.getFields().stream()
                .map(f -> f.getFieldName())
                .collect(Collectors.joining(", "));
        String careers = m.getCareers().stream()
                .map(c -> {
                    String careerString = c.getCompany();
                    if (c.getPeriod() != null && !c.getPeriod().isEmpty()) {
                        careerString += " (" + c.getPeriod() + ")";
                    }
                    return careerString;
                })
                .collect(Collectors.joining(", "));

        String certifications = m.getCertifications().stream()
                .map(c -> c.getCertDesc())
                .collect(Collectors.joining(", "));

        return MentorDto.builder()
                .nickname(m.getUser().getNickname())
                .mentoringTitle(m.getMentoringTitle()) // 멘토링 제목.
                .hourlyRate(m.getHourlyRate())       // 멘토링 시간당 금액.
                .intro(m.getIntro())
                .fieldName(fields)
                .careerDesc(careers)
                .thumbnail(m.getThumbnail())
                .rating(rating)
                .reviewCount(reviewCount)
                .build();
    }

    /**
     * MPR-004: 멘토 소개글을 수정합니다.
     * @param mentorId 현재 인증된 멘토의 ID
     * @param updateDto 수정할 멘토 프로필 정보
     * @return 업데이트된 멘토 프로필 정보
     */
    @Transactional
    public MentorUserDto updateMentorProfile(Long mentorId, MentorProfileUpdateDto updateDto) {
        MentorUser mentorUser = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new IllegalArgumentException("멘토를 찾을 수 없습니다."));

        // 1. 직접적인 필드 업데이트
        mentorUser.setThumbnail(updateDto.getThumbnail());
        mentorUser.setIntro(updateDto.getIntro());
        mentorUser.setMentoringTitle(updateDto.getMentoringTitle());
        mentorUser.setHourlyRate(updateDto.getHourlyRate());

        // 2. Field 업데이트
        fieldRepository.deleteByMentor(mentorUser); // 기존 Field 삭제
        if (updateDto.getFields() != null) {
            updateDto.getFields().forEach(fieldDto -> {
                Field field = new Field();
                field.setMentor(mentorUser);
                field.setFieldName(fieldDto.getFieldName());
                fieldRepository.save(field);
            });
        }

        // 3. Career 업데이트
        careerRepository.deleteByMentor(mentorUser); // 기존 Career 삭제
        if (updateDto.getCareers() != null) {
            updateDto.getCareers().forEach(careerDto -> {
                Career career = new Career();
                career.setMentor(mentorUser);
                career.setCompany(careerDto.getCompany());
                career.setPeriod(careerDto.getPeriod());
                careerRepository.save(career);
            });
        }

        // 4. Certification 업데이트
        certificationRepository.deleteByMentor(mentorUser); // 기존 Certification 삭제
        if (updateDto.getCertifications() != null) {
            updateDto.getCertifications().forEach(certificationDto -> {
                Certification certification = new Certification();
                certification.setMentor(mentorUser);
                certification.setCertDesc(certificationDto.getCertDesc());
                certificationRepository.save(certification);
            });
        }

        MentorUser savedMentorUser = mentorRepository.save(mentorUser);

        // 응답 DTO 변환
        return MentorUserDto.builder()
                .id(savedMentorUser.getId())
                .intro(savedMentorUser.getIntro())
                .field(savedMentorUser.getFields().stream().map(Field::getFieldName).collect(Collectors.joining(", ")))
                .career(savedMentorUser.getCareers().stream().map(c -> c.getCompany() + " (" + c.getPeriod() + ")").collect(Collectors.joining(", ")))
                .cert(savedMentorUser.getCertifications().stream().map(Certification::getCertDesc).collect(Collectors.joining(", ")))
                .mentoringTitle(savedMentorUser.getMentoringTitle())
                .hourlyRate(savedMentorUser.getHourlyRate())
                .updatedAt(savedMentorUser.getUpdatedAt())
                .build();
    }
}