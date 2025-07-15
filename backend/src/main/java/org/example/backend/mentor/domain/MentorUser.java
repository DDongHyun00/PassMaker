
package org.example.backend.mentor.domain;

import jakarta.persistence.*;
import lombok.*;
import org.example.backend.common.BaseTimeEntity;
import org.example.backend.user.domain.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorUser extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 255)
    private String thumbnail;

    @Column(columnDefinition = "TEXT")
    private String intro;

    /** 멘토링 세션 제목 */
    @Column(name = "mentoring_title", length = 100, nullable = false)
    private String mentoringTitle;      // Korean: 멘토링 세션 제목

    /** 멘토링 시간당 요금 (원) */
    @Column(name = "hourly_rate", nullable = false)
    private Integer hourlyRate;         // Korean: 멘토링 시간당 요금

    @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Field> fields = new ArrayList<>();

    @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Career> careers = new ArrayList<>();
}
