package org.example.backend.room.repository;

import org.example.backend.room.domain.MentoringRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MentoringRoomRepository extends JpaRepository<MentoringRoom, Long> {
    Optional<MentoringRoom> findByRoomCode(String roomCode);
}
