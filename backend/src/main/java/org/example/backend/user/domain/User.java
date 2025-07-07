
package org.example.backend.user.domain;

import jakarta.persistence.*;
import lombok.*;
import org.example.backend.common.BaseTimeEntity;
import org.example.backend.mentor.domain.MentorApply;
import org.example.backend.mentor.domain.MentorUser;
import org.hibernate.annotations.ColumnDefault;

import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User extends BaseTimeEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true, unique = true)        // 소셜로그인 유저의 경우 null이 들어갈 수 있음.
    private String email;

    @Column(nullable = true)                       // 소셜로그인 유저의 경우 null이 들어갈 수 있음.
    private String password;

    @Column(nullable = true)
    private String thumbnail;

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean isMentor;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false, unique = true)
    private String nickname;


    // === 연관 관계 ===

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<MentorUser> mentorUsers;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<MentorApply> mentorApplications;

}
