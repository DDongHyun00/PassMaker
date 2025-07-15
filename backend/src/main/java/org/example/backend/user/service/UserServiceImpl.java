package org.example.backend.user.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.reservation.domain.MentoringReservation;
import org.example.backend.reservation.dto.ReservationDto;
import org.example.backend.reservation.repository.ReservationRepository;
import org.example.backend.user.domain.User;
import org.example.backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    @Override
    public List<ReservationDto> getMyReservations(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<MentoringReservation> reservations = reservationRepository.findByUser(user);

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
