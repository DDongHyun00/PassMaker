package org.example.backend.mentor.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class AvailableTimeRequestDto {
    private Long mentorId;
    private List<AvailableSlot> availableSlots;

    @Getter
    @Setter
    public static class AvailableSlot {
        private DayOfWeek dayOfWeek;
        private LocalTime startTime;
        private LocalTime endTime;
    }
}
