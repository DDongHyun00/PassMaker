package org.example.backend.reservation.repository;

import org.example.backend.reservation.domain.MentoringReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

// ✅ JPA Repository - 예약 중복 여부 체크 메서드 추가
public interface MentoringReservationRepository extends JpaRepository<MentoringReservation, Long> {
  // Long: MentoringReservation의 PK 타입
  /**
   * 해당 멘토가 특정 시간에 이미 예약되어 있는지 검사합니다.
   * @param mentorId 멘토 ID
   * @param reservationTime 예약 시간
   * @return 중복 예약 존재 여부
   */
  boolean existsByMentorIdAndReservationTime(Long mentorId, LocalDateTime reservationTime);
}





