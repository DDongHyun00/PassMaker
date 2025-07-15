package org.example.backend.review.dto;

import jakarta.persistence.*;
import lombok.*;
import org.example.backend.common.BaseTimeEntity;
import org.example.backend.user.domain.User;
import org.example.backend.review.domain.Review;

@Entity
@Table(name = "review_report")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewReport extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @Column(nullable = false, length = 100)
    private String reason;

    @Column(length = 500)
    private String detail;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReportStatus status = ReportStatus.PENDING;
}