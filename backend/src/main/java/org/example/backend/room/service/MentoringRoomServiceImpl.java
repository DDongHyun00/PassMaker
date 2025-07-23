package org.example.backend.room.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.backend.reservation.domain.MentoringReservation;
import org.example.backend.room.domain.MentoringRoom;
import org.example.backend.room.dto.MentoringRoomEnterResponseDTO;
import org.example.backend.room.repository.MentoringRoomRepository;
import org.example.backend.stt.service.SttService; // ✅ STT 요약 서비스 사용
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MentoringRoomServiceImpl implements MentoringRoomService {

    private final MentoringRoomRepository mentoringRoomRepository;
    private final SttService sttService; // ✅ 요약 실행 위해 주입

    @Override
    public MentoringRoom createRoomFromReservation(MentoringReservation reservation) {
        LocalDateTime time = reservation.getReservationTime();

        MentoringRoom room = MentoringRoom.builder()
            .mentor(reservation.getMentor())
            .user(reservation.getUser())
            .reservation(reservation)
            .roomCode(generateRoomCode())
            .startedAt(time.minusMinutes(10))
            .endedAt(time.plusMinutes(70))
            .build();

        return mentoringRoomRepository.save(room);
    }

    private String generateRoomCode() {
        return RandomStringUtils.randomAlphanumeric(6).toUpperCase();
    }

    @Override
    public void validateRoomEntry(String roomCode) {
        MentoringRoom room = mentoringRoomRepository.findByRoomCode(roomCode)
            .orElseThrow(() -> new RuntimeException("존재하지 않는 방입니다."));

        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(room.getStartedAt())) {
            throw new RuntimeException("입장 가능한 시간이 아직 아닙니다.");
        }

        if (now.isAfter(room.getEndedAt())) {
            throw new RuntimeException("입장 시간이 지났습니다.");
        }
    }

    @Override
    public MentoringRoomEnterResponseDTO enterRoom(Long roomId, String inputCode, String username) {
        MentoringRoom room = mentoringRoomRepository.findById(roomId)
            .orElseThrow(() -> new RuntimeException("해당 방이 존재하지 않습니다."));

        if (!room.getRoomCode().equals(inputCode)) {
            throw new RuntimeException("방 코드가 일치하지 않습니다.");
        }

        MentoringReservation reservation = room.getReservation();
        String mentorUsername = reservation.getMentor().getUser().getEmail();
        String menteeUsername = reservation.getUser().getEmail();

        boolean isMentor = mentorUsername.equals(username);
        boolean isMentee = menteeUsername.equals(username);

        if (!isMentor && !isMentee) {
            throw new RuntimeException("이 방에 입장할 권한이 없습니다.");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(room.getStartedAt())) {
            throw new RuntimeException("입장 가능한 시간이 아직 아닙니다.");
        }

        if (now.isAfter(room.getEndedAt())) {
            throw new RuntimeException("입장 시간이 지났습니다.");
        }

        return new MentoringRoomEnterResponseDTO(
            room.getRoomId(),
            room.getRoomCode(),
            room.getStartedAt(),
            room.getEndedAt(),
            username,
            isMentor ? "MENTOR" : "MENTEE",
            isMentor ? menteeUsername : mentorUsername,
            isMentor ? "MENTEE" : "MENTOR"
        );
    }

    @Override
    public void closeRoom(Long roomId) {
        MentoringRoom room = mentoringRoomRepository.findById(roomId)
            .orElseThrow(() -> new IllegalArgumentException("해당 방이 존재하지 않습니다."));

        // (선택) 방 상태 변경 로직 예시
        // room.setStatus(RoomStatus.CLOSED);

        mentoringRoomRepository.save(room);

        // ✅ STT 전체 요약 실행
        sttService.summarize(roomId);
    }

}
