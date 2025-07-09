
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
    private Long mentorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 255)
    private String thumbnail;

    @Column(length = 255)
    private String intro;

    @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Field> fields = new ArrayList<>();

    @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Career> careers = new ArrayList<>();
}
