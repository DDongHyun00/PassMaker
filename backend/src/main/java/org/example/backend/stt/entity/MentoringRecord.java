package org.example.backend.stt.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.backend.room.domain.MentoringRoom;

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

    @Column(name = "part_index")
    private Integer partIndex;  // STT 파트 번호 (요약이면 null)

    @Lob
    private String summaryText;
}
