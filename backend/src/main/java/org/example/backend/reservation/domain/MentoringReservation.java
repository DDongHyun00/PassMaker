package org.example.backend.reservation.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.backend.common.BaseTimeEntity;
import org.example.backend.mentor.domain.MentorUser;
import org.example.backend.payment.domain.Payment;
import org.example.backend.payment.domain.PaymentStatus;
import org.example.backend.room.domain.MentoringRoom;
import org.example.backend.user.domain.User;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentoringReservation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reserve_id") // 이 부분 중요!
    private Long reserveId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private MentorUser mentor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(mappedBy = "reservation", fetch = FetchType.LAZY)
    private Payment payment;

    @OneToOne(mappedBy = "reservation", fetch = FetchType.LAZY)
    private MentoringRoom room;

    @NotNull
    private LocalDateTime reservationTime;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status;

    // ✅ 상태 전환 메서드
    public void approve() {
        this.status = ReservationStatus.ACCEPT;
    }

    public void reject() {
        this.status = ReservationStatus.REJECT;
    }

    // ✅ 선택적: 결제 여부 확인 메서드 (null-safe)
    public boolean isPaid() {
        return payment != null && payment.getStatus() == PaymentStatus.PAID;
    }

    public boolean isCancelable() {
        return payment != null && payment.getStatus() == PaymentStatus.PAID &&
            status == ReservationStatus.WAITING;
    }
}
