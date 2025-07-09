package org.example.backend.room.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentoringRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private MentoringRoom room;

    @Lob
    private String sttText;

    @Lob
    private String summaryText;
}
