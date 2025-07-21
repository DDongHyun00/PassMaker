
package org.example.backend.mentor.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.mentor.dto.MentorDto;
import org.example.backend.mentor.dto.MentorProfileUpdateDto;
import org.example.backend.mentor.dto.MentorSimpleDto;
import org.example.backend.mentor.dto.MentorUserDto;
import org.example.backend.mentor.dto.MentorProfileResponseDto; // [수정] 응답 DTO import 추가
import org.example.backend.mentor.repository.MentorUserRepository;
import org.example.backend.mentor.service.MentorService;
import org.example.backend.mentor.service.MentorReservationService; // 추가
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.example.backend.reservation.dto.ReservationDto;
import org.example.backend.reservation.dto.ReservationActionDto;
import org.example.backend.auth.domain.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.example.backend.mentor.dto.AvailableTimeRequestDto;
import org.example.backend.mentor.dto.AvailableTimeDto;
import org.example.backend.mentor.dto.MentorStatusDto;
import org.example.backend.mentor.dto.MentorStatusUpdateDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/mentors")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class MentorController {

    private final MentorUserRepository mentorUserRepository;
    private final MentorService mentorService;
    private final MentorReservationService mentorReservationService; // 추가

    @GetMapping
    public ResponseEntity<List<MentorDto>> getAllMentors() {
        return ResponseEntity.ok(mentorService.getAllMentors());
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationDto>> getMentorReservations(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long mentorId = userDetails.getUserId(); // 멘토의 ID
        List<ReservationDto> reservations = mentorReservationService.getMentorReservations(mentorId);
        return ResponseEntity.ok(reservations);
    }

    @PatchMapping("/reservations/{reservationId}/action")
    public ResponseEntity<ReservationDto> acceptOrRejectReservation(
            @PathVariable Long reservationId,
            @RequestBody ReservationActionDto actionDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long mentorId = userDetails.getUserId();
        ReservationDto updatedReservation = mentorReservationService.acceptOrRejectReservation(reservationId, actionDto.getAction(), mentorId);
        return ResponseEntity.ok(updatedReservation);
    }

    /**
     * MPR-004: 멘토 소개글을 수정합니다.
     * PUT /api/mentors/me/edit-profile
     * @param updateDto 수정할 멘토 프로필 정보
     * @param userDetails 현재 인증된 멘토의 정보
     * @return 업데이트된 멘토 프로필 정보
     */

    /**
     * [추가] 현재 로그인한 멘토의 프로필 설정 정보를 조회합니다.
     */
    @GetMapping("/me/profile")
    public ResponseEntity<MentorProfileResponseDto> getMentorProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId(); // 변수명을 mentorId -> userId로 변경
        MentorProfileResponseDto profile = mentorService.getMentorProfile(userId);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/me/edit-profile")
    public ResponseEntity<MentorUserDto> updateMentorProfile(
            @RequestBody MentorProfileUpdateDto updateDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long mentorId = userDetails.getUserId();
        MentorUserDto updatedMentor = mentorService.updateMentorProfile(mentorId, updateDto);
        return ResponseEntity.ok(updatedMentor);
    }

    /**
     * MPR-005: 멘토링 가능한 시간 설정
     * 멘토가 특정 요일 및 시간대를 설정하여 가능한 시간을 등록합니다.
     * PUT /api/mentors/me/available-times
     * @param requestDto 설정할 가용 시간 목록을 포함하는 DTO
     * @param userDetails 현재 인증된 멘토의 정보
     * @return 저장된 가용 시간 목록을 포함하는 응답 DTO
     */
    @PutMapping("/me/available-times")
    public ResponseEntity<AvailableTimeDto> setMentorAvailableTimes(
            @RequestBody AvailableTimeRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long mentorId = userDetails.getUserId();
        AvailableTimeDto responseDto = mentorService.setMentorAvailableTimes(mentorId, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * MPR-005: 멘토링 가능한 시간 조회
     * 특정 멘토의 설정된 가용 시간 목록을 조회합니다.
     * GET /api/mentors/me/available-times
     * @param userDetails 현재 인증된 멘토의 정보
     * @return 조회된 가용 시간 목록을 포함하는 응답 DTO
     */
    @GetMapping("/me/available-times")
    public ResponseEntity<AvailableTimeDto> getMentorAvailableTimes(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long mentorId = userDetails.getUserId();
        AvailableTimeDto responseDto = mentorService.getMentorAvailableTimes(mentorId);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/id/{mentorId}")
    @Transactional(readOnly = true)
    public ResponseEntity<MentorSimpleDto> getMentorNicknameById(@PathVariable Long mentorId) {
        return mentorUserRepository.findById(mentorId)
            .map(mentorUser -> {
                String nickname = mentorUser.getUser().getNickname();
                return ResponseEntity.ok(new MentorSimpleDto(mentorId, nickname));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * MPR-006: 멘토 상태 ON/OFF
     * 멘토가 현재 활동 상태(모집 중 / 비활성)를 ON/OFF 전환할 수 있습니다.
     * PATCH /api/mentors/me/status
     * @param updateDto 변경할 멘토의 활동 상태를 포함하는 DTO
     * @param userDetails 현재 인증된 멘토의 정보
     * @return 업데이트된 멘토의 활동 상태를 포함하는 응답 DTO
     */
    @PatchMapping("/me/status")
    public ResponseEntity<MentorStatusDto> updateMentorStatus(
            @RequestBody MentorStatusUpdateDto updateDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("MPR-006: 컨트롤러 진입 - 멘토 상태 업데이트 요청. isActive: {}", updateDto.isActive());
        Long mentorId = userDetails.getUserId();
        MentorStatusDto responseDto = mentorService.updateMentorStatus(mentorId, updateDto);
        log.info("MPR-006: 컨트롤러 응답 - 멘토 상태 업데이트 완료. mentorId: {}, isActive: {}", responseDto.getMentorId(), responseDto.isActive());
        return ResponseEntity.ok(responseDto);
    }
}
