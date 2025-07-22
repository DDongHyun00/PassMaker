package org.example.backend.stt.repository;

import org.example.backend.stt.entity.MentoringRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MentoringRecordRepository extends JpaRepository<MentoringRecord, Long> {

  /**
   * 파트별로 저장된 STT 레코드를 partIndex 순서대로 모두 조회
   */
  List<MentoringRecord> findAllByRoom_RoomIdOrderByPartIndex(Long roomId);

  /**
   * 최종 요약용 레코드 (partIndex == null) 단건 조회
   */
  Optional<MentoringRecord> findByRoom_RoomIdAndPartIndexIsNull(Long roomId);

  /**
   * (이전 누적 기능에서 사용하던 메서드가 아직 필요한 경우)
   * roomId 단건 조회
   */
  Optional<MentoringRecord> findByRoom_RoomId(Long roomId);

  List<MentoringRecord> findAllByRoom_RoomIdOrderByRecordId(Long roomId);
}

/*구조 요약
MentoringRecord를 기본 키(Long) 기반으로 CRUD
나중에 findByRoomId(Long roomId) 같은 커스텀 메서드도 추가할 수 있음 (예: 마이페이지에서 조회할 때)*/