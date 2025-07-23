package org.example.backend.stt.repository;

import org.example.backend.stt.entity.MentoringRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MentoringRecordRepository extends JpaRepository<MentoringRecord, Long> {

  /** STT 조각을 partIndex 순서대로 조회 (upload-audio → 누적용) */
  List<MentoringRecord> findAllByRoom_RoomIdOrderByPartIndex(Long roomId);

  /** STT 조각을 recordId 순서대로 조회 (summarize에서 텍스트 합칠 때) */
  List<MentoringRecord> findAllByRoom_RoomIdOrderByRecordId(Long roomId);

  /** 모든 방의 요약 레코드( partIndex == null ) 조회 */
  List<MentoringRecord> findAllByPartIndexIsNull();

  /** 특정 방의 요약 레코드 단일 조회 ( partIndex == null ) */
  Optional<MentoringRecord> findByRoom_RoomIdAndPartIndexIsNull(Long roomId);

  /*(이전 누적 기능에서 사용하던 메서드가 아직 필요한 경우)
//   * roomId 단건 조회*/
  Optional<MentoringRecord> findByRoom_RoomId(Long roomId);
}
//*구조 요약
//MentoringRecord를 기본 키(Long) 기반으로 CRUD
//나중에 findByRoomId(Long roomId) 같은 커스텀 메서드도 추가할 수 있음 (예: 마이페이지에서 조회할 때)*/