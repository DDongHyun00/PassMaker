package org.example.backend.inquiry.domain;

import jakarta.persistence.*;
import lombok.*;
import org.example.backend.common.BaseTimeEntity;
import org.example.backend.user.domain.User;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Inquiry extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String inquiryTitle;

    @Column(nullable = false)
    private String inquiryContent;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, length = 20)
    private InquiryStatus inquiryStatus = InquiryStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InquiryType inquiryType;

    private String respondTitle;
    private String respondContent;
}
