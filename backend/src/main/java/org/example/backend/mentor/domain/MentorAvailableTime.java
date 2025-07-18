package org.example.backend.mentor.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.DayOfWeek; // 요일을 나타내는 Enum
import java.time.LocalTime; // 시간만 나타내는 클래스

/**
 * 멘토의 반복 가능한 멘토링 가용 시간을 정의하는 엔티티입니다.
 * 멘토가 특정 요일의 특정 시간대에 멘토링이 가능함을 나타냅니다.
 * 예를 들어, '매주 월요일 09:00 ~ 10:00'와 같은 반복적인 가용 시간을 저장합니다.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorAvailableTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유 식별자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private MentorUser mentor; // 해당 가용 시간을 설정하는 멘토 (MentorUser 엔티티와 다대일 관계)

    @NotNull
    @Enumerated(EnumType.STRING) // DayOfWeek Enum 값을 문자열로 DB에 저장 (예: MONDAY, TUESDAY)
    private DayOfWeek dayOfWeek; // 멘토링이 가능한 요일

    @NotNull
    private LocalTime startTime; // 해당 요일에 멘토링이 시작 가능한 시간 (예: 09:00)

    @NotNull
    private LocalTime endTime; // 해당 요일에 멘토링이 종료 가능한 시간 (예: 10:00)
}
