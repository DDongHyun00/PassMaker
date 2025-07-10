package org.example.backend.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.backend.room.domain.MentoringRoom;

@Getter
@AllArgsConstructor
public class ApproveReservationResponseDTO {
    private Long roomId;
    private String roomCode;
    private String mentorNickname;
    private String menteeNickname;

    public static ApproveReservationResponseDTO of(MentoringRoom room) {
        return new ApproveReservationResponseDTO(
                room.getRoomId(),
                room.getRoomCode(),
                room.getMentor().getUser().getNickname(),
                room.getUser().getNickname()
        );
    }
}

