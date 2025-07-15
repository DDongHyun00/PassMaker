package org.example.backend.mentor.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.mentor.domain.MentorUser;
import org.example.backend.mentor.repository.MentorRepository;
import org.example.backend.reservation.domain.MentoringReservation;
import org.example.backend.reservation.domain.ReservationStatus;
import org.example.backend.reservation.dto.ReservationDto;
import org.example.backend.reservation.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MentorReservationService {

    private final MentorRepository mentorRepository;
    private final ReservationRepository reservationRepository;

    public List<ReservationDto> getMentorReservations(Long mentorId) {
        MentorUser mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new IllegalArgumentException("Mentor not found"));

        List<MentoringReservation> reservations = reservationRepository.findByMentor(mentor);

        return reservations.stream()
                .map(reservation -> ReservationDto.builder()
                        .reservationId(reservation.getReserveId())
                        .mentorName(reservation.getMentor().getUser().getNickname())
                        .reservationTime(reservation.getReservationTime())
                        .status(reservation.getStatus())
                        .statusLabel(getStatusLabel(reservation.getStatus()))
                        .statusColor(getStatusColor(reservation.getStatus()))
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public ReservationDto acceptOrRejectReservation(Long reservationId, String action, Long mentorId) {
        MentoringReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        if (!reservation.getMentor().equals(mentorId)) {
            throw new IllegalArgumentException("You are not authorized to perform this action for this reservation.");
        }

        if ("ACCEPT".equalsIgnoreCase(action)) {
            reservation.setStatus(ReservationStatus.ACCEPT);
        } else if ("REJECT".equalsIgnoreCase(action)) {
            reservation.setStatus(ReservationStatus.REJECT);
        } else {
            throw new IllegalArgumentException("Invalid action: " + action);
        }

        MentoringReservation updatedReservation = reservationRepository.save(reservation);

        return ReservationDto.builder()
                .reservationId(updatedReservation.getReserveId())
                .mentorName(updatedReservation.getMentor().getUser().getNickname())
                .reservationTime(updatedReservation.getReservationTime())
                .status(updatedReservation.getStatus())
                .statusLabel(getStatusLabel(updatedReservation.getStatus()))
                .statusColor(getStatusColor(updatedReservation.getStatus()))
                .build();
    }

    private String getStatusLabel(org.example.backend.reservation.domain.ReservationStatus status) {
        switch (status) {
            case WAITING:
                return "대기";
            case ACCEPT:
                return "확정";
            case REJECT:
                return "취소";
            default:
                return "알 수 없음";
        }
    }

    private String getStatusColor(org.example.backend.reservation.domain.ReservationStatus status) {
        switch (status) {
            case WAITING:
                return "#FFD700"; // Gold
            case ACCEPT:
                return "#32CD32"; // LimeGreen
            case REJECT:
                return "#DC143C"; // Crimson
            default:
                return "#808080"; // Gray
        }
    }
}
