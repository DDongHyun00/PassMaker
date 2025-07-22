// src/main/java/org/example/backend/stt/entity/MentoringRecord.java
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

    // ▶ 1:N 관계로 변경: 한 방(room)에 여러 STT 레코드 저장 가능
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private MentoringRoom room;

    @Lob
    private String sttText;

    @Column(name = "part_index")
    private Integer partIndex;  // STT 파트 번호 (요약이면 null)

    @Lob
    private String summaryText;
}
