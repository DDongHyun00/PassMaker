package org.example.backend.user.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.reservation.domain.MentoringReservation;
import org.example.backend.reservation.dto.ReservationDto;
import org.example.backend.reservation.repository.ReservationRepository;
import org.example.backend.user.domain.User;
import org.example.backend.user.dto.FindEmailRequestDto;
import org.example.backend.user.dto.FindEmailResponseDto;
import org.example.backend.user.dto.ResetPasswordRequestDto;
import org.example.backend.user.dto.ResetPasswordResponseDto;
import org.example.backend.user.repository.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

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

    public FindEmailResponseDto findEmail(FindEmailRequestDto request) {
        User user = userRepository.findByNameAndPhone(
                request.getName(), request.getPhone()
        ).orElseThrow(() -> new IllegalArgumentException("일치하는 사용자가 없습니다."));

        return new FindEmailResponseDto(user.getEmail());
    }


    // 비밀번호 찾기
    @Override
    @Transactional
    public ResetPasswordResponseDto resetPassword(ResetPasswordRequestDto requestDto) {
        String email = requestDto.getEmail();
        String phone = requestDto.getPhone();

        // 1. 사용자 검증 (email + phone)
        User user = userRepository.findByEmailAndPhone(email, phone)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 사용자가 없습니다."));

        // 2. 임시 비밀번호 생성
        String tempPassword = UUID.randomUUID().toString().substring(0, 10);

        // 3. 비밀번호 암호화 & 저장
        user.setPassword(passwordEncoder.encode(tempPassword));
        userRepository.save(user);

        // 4. 이메일 발송
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setFrom("kd3573@naver.com"); // 실제 SMTP 계정과 동일하게 설정해야 함
        message.setSubject("[PassMaker] 임시 비밀번호 안내");
        message.setText("임시 비밀번호는 다음과 같습니다: " + tempPassword + "\n로그인 후 반드시 비밀번호를 변경해주세요.");
        mailSender.send(message);

        return new ResetPasswordResponseDto("임시 비밀번호가 이메일로 전송되었습니다.");
    }

}
