
package org.example.backend.reservation.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.backend.common.BaseTimeEntity;
import org.example.backend.mentor.domain.MentorUser;
import org.example.backend.payment.domain.Payment;
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
    private Long reserveId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private MentorUser mentor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(mappedBy = "reservation", fetch = FetchType.LAZY)
    private Payment payment;

    @NotNull
    private LocalDateTime reservationTime;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    public void approve() {
        this.status = ReservationStatus.ACCEPT;
    }

    public void reject() {
        this.status = ReservationStatus.REJECT;
    }
}

//    | 개념                        | 설명                                |
//    | ---------------------- | ---------------------------            |
//    | JPA Entity             | 실제 DB 테이블과 매핑되는 Java 클래스       |
//    | @ManyToOne / @OneToOne | 객체 간 관계 정의 (연관관계 매핑)           |
//    | 지연 로딩(LAZY)          | 객체 관계를 즉시가 아닌 **필요할 때만 조회** |
//    | EnumType.STRING        | enum 값을 문자열로 안전하게 저장           |
//    | Lombok                 | 코드를 간결하게 만드는 자동 생성 어노테이션   |
//    | 생성/수정 시간 상속       | 공통 기능(BaseTimeEntity) 재사용          |
