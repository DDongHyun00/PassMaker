package org.example.backend.stt.repository;

import org.example.backend.stt.entity.MentoringRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MentoringRecordRepository extends JpaRepository<MentoringRecord, Long> {

  // 필요 시 아래처럼 확장 가능:
  // Optional<MentoringRecord> findByRoomId(Long roomId);

  Optional<MentoringRecord> findByRoom_RoomId(Long roomId);
  List<MentoringRecord> findAllByRoom_RoomIdOrderByRecordId(Long roomId);
  Optional<MentoringRecord> findByRoom_RoomIdAndPartIndexIsNull(Long roomId);




}
/*구조 요약
MentoringRecord를 기본 키(Long) 기반으로 CRUD
나중에 findByRoomId(Long roomId) 같은 커스텀 메서드도 추가할 수 있음 (예: 마이페이지에서 조회할 때)*/