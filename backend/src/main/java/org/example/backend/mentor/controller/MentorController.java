
package org.example.backend.mentor.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.mentor.dto.MentorDto;
import org.example.backend.mentor.dto.MentorProfileUpdateDto;
import org.example.backend.mentor.dto.MentorSimpleDto;
import org.example.backend.mentor.dto.MentorUserDto;
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

import java.util.List;

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

    @PutMapping("/me/edit-profile")
    public ResponseEntity<MentorUserDto> updateMentorProfile(
            @RequestBody MentorProfileUpdateDto updateDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long mentorId = userDetails.getUserId();
        MentorUserDto updatedMentor = mentorService.updateMentorProfile(mentorId, updateDto);
        return ResponseEntity.ok(updatedMentor);
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
}
