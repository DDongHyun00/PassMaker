
package org.example.backend.mentor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.mentor.domain.Career;
import org.example.backend.mentor.domain.Certification;
import org.example.backend.mentor.domain.Field;
import org.example.backend.mentor.domain.MentorUser;
import org.example.backend.mentor.dto.MentorDto;
import org.example.backend.mentor.dto.MentorProfileUpdateDto;
import org.example.backend.mentor.dto.MentorUserDto;
import org.example.backend.mentor.dto.MentorProfileResponseDto;
import org.example.backend.mentor.dto.CareerDto;
import org.example.backend.mentor.dto.CertificationDto;
import org.example.backend.mentor.dto.FieldDto;
import org.example.backend.mentor.dto.MentorProfileResponseDto; // [수정] 응답 DTO import 추가
import org.example.backend.mentor.dto.CareerDto; // [수정] DTO import 추가
import org.example.backend.mentor.dto.CertificationDto; // [수정] DTO import 추가
import org.example.backend.mentor.dto.FieldDto; // [수정] DTO import 추가
import org.example.backend.mentor.repository.*;
import org.example.backend.review.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.example.backend.mentor.domain.MentorAvailableTime;
import org.example.backend.mentor.dto.AvailableTimeDto;
import org.example.backend.mentor.dto.AvailableTimeRequestDto;
import org.example.backend.mentor.dto.MentorStatusDto;
import org.example.backend.mentor.dto.MentorStatusUpdateDto;
import org.example.backend.user.domain.User; // [추가] User 임포트
import org.example.backend.user.repository.UserRepository; // [추가] UserRepository 임포트

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MentorService {

    private final MentorRepository mentorRepository;
    private final ReviewRepository reviewRepository;
    private final FieldRepository fieldRepository; // 추가
    private final CareerRepository careerRepository; // 추가
    private final CertificationRepository certificationRepository; // 추가
    private final MentorAvailableTimeRepository mentorAvailableTimeRepository; // MPR-005: 멘토 가용 시간 Repository 추가
    private final UserRepository userRepository; // [추가] UserRepository 주입


    /**
     * [수정] 현재 로그인한 멘토의 프로필 설정 정보를 조회합니다.
     */
    public MentorProfileResponseDto getMentorProfile(Long userId) { // 파라미터를 mentorId -> userId로 변경
        // userId를 사용하여 User 엔티티 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다. userId=" + userId));

        // User 엔티티를 사용하여 MentorUser 조회
        MentorUser mentorUser = mentorRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저에 연결된 멘토 정보를 찾을 수 없습니다. userId=" + userId));

        List<FieldDto> fields = mentorUser.getFields().stream()
                .map(f -> new FieldDto(f.getFieldName()))
                .collect(Collectors.toList());

        List<CareerDto> careers = mentorUser.getCareers().stream()
                .map(c -> new CareerDto(c.getCompany(), c.getPeriod()))
                .collect(Collectors.toList());

        List<CertificationDto> certifications = mentorUser.getCertifications().stream()
                .map(c -> new CertificationDto(c.getCertDesc()))
                .collect(Collectors.toList());

        return MentorProfileResponseDto.builder()
                .thumbnail(mentorUser.getThumbnail())
                .intro(mentorUser.getIntro())
                .mentoringTitle(mentorUser.getMentoringTitle())
                .hourlyRate(mentorUser.getHourlyRate())
                .fields(fields)
                .careers(careers)
                .certifications(certifications)
                .build();
    }


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

        // ✨ 5. MentorAvailableTime 업데이트 추가
        mentorAvailableTimeRepository.deleteByMentorId(mentorId); // 기존 시간 삭제
        if (updateDto.getAvailableTimes() != null) {
            updateDto.getAvailableTimes().forEach(slot -> {
                MentorAvailableTime availableTime = MentorAvailableTime.builder()
                        .mentor(mentorUser)
                        .dayOfWeek(slot.getDayOfWeek())
                        .startTime(slot.getStartTime())
                        .endTime(slot.getEndTime())
                        .build();
                mentorAvailableTimeRepository.save(availableTime);
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

    /**
     * MPR-005: 멘토링 가능한 시간 설정
     * 멘토가 특정 요일 및 시간대를 설정하여 가능한 시간을 등록합니다.
     * 기존에 설정된 모든 가용 시간을 삭제하고, 새로운 가용 시간 목록으로 갱신합니다.
     * @param mentorId 멘토의 고유 ID
     * @param requestDto 설정할 가용 시간 목록을 포함하는 DTO
     * @return 저장된 가용 시간 목록을 포함하는 응답 DTO
     */
    @Transactional
    public AvailableTimeDto setMentorAvailableTimes(Long mentorId, AvailableTimeRequestDto requestDto) {
        MentorUser mentorUser = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new IllegalArgumentException("멘토를 찾을 수 없습니다."));

        // 기존 가용 시간 모두 삭제
        mentorAvailableTimeRepository.deleteByMentorId(mentorId);

        // 새로운 가용 시간 저장
        List<MentorAvailableTime> savedAvailableTimes = requestDto.getAvailableSlots().stream()
                .map(slot -> MentorAvailableTime.builder()
                        .mentor(mentorUser)
                        .dayOfWeek(slot.getDayOfWeek())
                        .startTime(slot.getStartTime())
                        .endTime(slot.getEndTime())
                        .build())
                .map(mentorAvailableTimeRepository::save)
                .collect(Collectors.toList());

        // 응답 DTO 생성
        List<AvailableTimeDto.SavedSlot> savedSlots = savedAvailableTimes.stream()
                .map(at -> {
                    AvailableTimeDto.SavedSlot slot = new AvailableTimeDto.SavedSlot();
                    slot.setId(at.getId());
                    slot.setDayOfWeek(at.getDayOfWeek());
                    slot.setStartTime(at.getStartTime());
                    slot.setEndTime(at.getEndTime());
                    return slot;
                })
                .collect(Collectors.toList());

        AvailableTimeDto responseDto = new AvailableTimeDto();
        responseDto.setMentorId(mentorId);
        responseDto.setSavedSlots(savedSlots);

        return responseDto;
    }

    /**
     * MPR-005: 멘토링 가능한 시간 조회
     * 특정 멘토의 설정된 가용 시간 목록을 조회합니다.
     * @param mentorId 멘토의 고유 ID
     * @return 조회된 가용 시간 목록을 포함하는 응답 DTO
     */
    @Transactional(readOnly = true)
    public AvailableTimeDto getMentorAvailableTimes(Long mentorId) {
        // 멘토 존재 여부 확인 (선택 사항, 필요시 추가)
        // mentorRepository.findById(mentorId).orElseThrow(() -> new IllegalArgumentException("멘토를 찾을 수 없습니다."));

        List<MentorAvailableTime> availableTimes = mentorAvailableTimeRepository.findByMentorId(mentorId);

        List<AvailableTimeDto.SavedSlot> savedSlots = availableTimes.stream()
                .map(at -> {
                    AvailableTimeDto.SavedSlot slot = new AvailableTimeDto.SavedSlot();
                    slot.setId(at.getId());
                    slot.setDayOfWeek(at.getDayOfWeek());
                    slot.setStartTime(at.getStartTime());
                    slot.setEndTime(at.getEndTime());
                    return slot;
                })
                .collect(Collectors.toList());

        AvailableTimeDto responseDto = new AvailableTimeDto();
        responseDto.setMentorId(mentorId);
        responseDto.setSavedSlots(savedSlots);

        return responseDto;
    }

    /**
     * MPR-006: 멘토 상태 ON/OFF
     * 멘토의 현재 활동 상태(모집 중 / 비활성)를 ON/OFF 전환합니다.
     * @param mentorId 멘토의 고유 ID
     * @param updateDto 변경할 멘토의 활동 상태를 포함하는 DTO
     * @return 업데이트된 멘토의 활동 상태를 포함하는 응답 DTO
     */
    @Transactional
    public MentorStatusDto updateMentorStatus(Long userId, MentorStatusUpdateDto updateDto) { // [수정] mentorId -> userId로 파라미터 변경
        log.info("MPR-006: 멘토 상태 업데이트 요청 - userId: {}, isActive: {}", userId, updateDto.isActive());

        // [수정] userId를 사용하여 User 엔티티 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다. userId=" + userId));

        // [수정] User 엔티티를 사용하여 MentorUser 조회
        MentorUser mentorUser = mentorRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저에 연결된 멘토 정보를 찾을 수 없습니다. userId=" + userId));

        log.info("MPR-006: 멘토 찾음 - 현재 isActive: {}", mentorUser.isActive());
        mentorUser.setActive(updateDto.isActive()); // 멘토의 활동 상태 업데이트
        log.info("MPR-006: isActive 설정 후 - 변경될 isActive: {}", mentorUser.isActive());

        MentorUser savedMentorUser = mentorRepository.save(mentorUser);
        log.info("MPR-006: 멘토 상태 저장 완료 - 저장된 isActive: {}", savedMentorUser.isActive());

        return MentorStatusDto.builder()
                .mentorId(savedMentorUser.getId())
                .isActive(savedMentorUser.isActive())
                .updatedAt(savedMentorUser.getUpdatedAt())
                .build();
    }
}