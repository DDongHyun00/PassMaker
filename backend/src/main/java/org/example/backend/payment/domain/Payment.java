package org.example.backend.payment.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.example.backend.common.BaseTimeEntity;
import org.example.backend.reservation.domain.MentoringReservation;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends BaseTimeEntity {

    @Id
    @Column(length = 255)
    private String payId; // 외부 결제 키 기반 ID

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private MentoringReservation reservation;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PaymentStatus status;

    @NotNull
    @Size(max = 255)
    private String paymentKey;

    @NotNull
    private Integer amount;

    private LocalDateTime approvedAt;
}