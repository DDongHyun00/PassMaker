package org.example.backend.room.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class MentoringRoomEnterResponseDTO {
    private Long roomId;
    private String roomCode;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    private String myUsername;
    private String myRole; // "MENTOR" or "MENTEE"

    private String opponentUsername;
    private String opponentRole;
}
