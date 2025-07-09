package org.example.backend.room.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.backend.common.BaseTimeEntity;
import org.example.backend.mentor.domain.MentorUser;
import org.example.backend.reservation.domain.MentoringReservation;
import org.example.backend.user.domain.User;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentoringRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private MentorUser mentor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserve_id", nullable = false)
    private MentoringReservation reservation;

    @NotNull
    @Column(length = 6)
    private String roomCode;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
}
