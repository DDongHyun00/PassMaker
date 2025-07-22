
package org.example.backend.mentor.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.example.backend.common.BaseTimeEntity;
import org.example.backend.user.domain.User;

import java.util.List;
import java.util.Set;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MentorApply extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT") // [수정] 자기소개 필드를 TEXT 타입으로 변경하여 길이 제한 제거
    private String intro;

    @Size(max = 255) // [추가] 멘토링 제목 필드
    private String mentoringTitle;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ApplyStatus status;

    @Size(max = 255)
    private String reason;

    @OneToMany(mappedBy = "apply", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ApplyField> applyFields;

    @OneToMany(mappedBy = "apply", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ApplyCareer> applyCareers;

    @OneToMany(mappedBy = "apply", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ApplyCertification> applyCertifications;
}
