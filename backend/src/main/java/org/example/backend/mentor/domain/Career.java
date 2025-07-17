package org.example.backend.mentor.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Career {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long careerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private MentorUser mentor;

    @NotNull
    @Size(max = 255)
    private String company;

    @NotNull
    @Size(max = 255)
    private String period;
}