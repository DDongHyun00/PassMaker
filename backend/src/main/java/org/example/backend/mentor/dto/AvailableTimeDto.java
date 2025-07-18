package org.example.backend.mentor.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class AvailableTimeDto {
    private Long mentorId;
    private List<SavedSlot> savedSlots;

    @Getter
    @Setter
    public static class SavedSlot {
        private Long id;
        private DayOfWeek dayOfWeek;
        private LocalTime startTime;
        private LocalTime endTime;
    }
}
