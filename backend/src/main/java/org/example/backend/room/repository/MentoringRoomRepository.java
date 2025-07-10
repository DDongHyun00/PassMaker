package org.example.backend.room.repository;

import org.example.backend.room.domain.MentoringRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentoringRoomRepository extends JpaRepository<MentoringRoom, Long> {
}
