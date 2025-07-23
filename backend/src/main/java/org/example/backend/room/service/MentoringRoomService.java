package org.example.backend.room.service;

import org.example.backend.reservation.domain.MentoringReservation;
import org.example.backend.room.domain.MentoringRoom;
import org.example.backend.room.dto.MentoringRoomEnterResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentoringRoomService {
    MentoringRoom createRoomFromReservation(MentoringReservation reservation);
    void validateRoomEntry(String roomCode);
    MentoringRoomEnterResponseDTO enterRoom(Long roomId, String inputCode, String username);
    void closeRoom(Long roomId);

}
