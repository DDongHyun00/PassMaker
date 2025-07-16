package org.example.backend.reservation.repository;

import org.example.backend.reservation.domain.MentoringReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface MentoringReservationRepository extends JpaRepository<MentoringReservation, Long> {

  /**
   * ✅ 기본 메서드: 정확히 같은 시간에 예약된 항목이 있는지
   */
  boolean existsByMentor_IdAndReservationTime(Long mentorId, LocalDateTime reservationTime);

  /**
   * ✅ 커스텀 JPQL - 초 단위 무시하고 분 단위까지만 비교
   * (중복 예약 방지용으로 사용됨)
   */
  @Query("""
      SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
      FROM MentoringReservation r
      WHERE r.mentor.id = :mentorId
        AND FUNCTION('DATE_FORMAT', r.reservationTime, '%Y-%m-%d %H:%i') =
            FUNCTION('DATE_FORMAT', :reservationTime, '%Y-%m-%d %H:%i')
  """)
  boolean existsByMentorIdAndTimeIgnoringSeconds(
      @Param("mentorId") Long mentorId,
      @Param("reservationTime") LocalDateTime reservationTime
  );
}
