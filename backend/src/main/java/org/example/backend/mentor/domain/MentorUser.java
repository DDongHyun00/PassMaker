
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

    /** 멘토의 활동 상태 (true: 모집 중, false: 비활성) */
    @Column(name = "is_active", nullable = false)
    private boolean isActive; // 멘토의 활동 상태

    @Builder.Default
    @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Field> fields = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Career> careers = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Certification> certifications = new ArrayList<>();

    public void addField(Field field) {
        field.setMentor(this);
        this.fields.add(field);
    }

    public void addCareer(Career career) {
        career.setMentor(this);
        this.careers.add(career);
    }
}
