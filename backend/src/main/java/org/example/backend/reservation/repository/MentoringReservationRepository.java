package org.example.backend.reservation.repository;

import org.example.backend.mentor.domain.MentorUser;
import org.example.backend.reservation.domain.MentoringReservation;
import org.example.backend.reservation.domain.ReservationStatus;
import org.example.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
  Optional<MentoringReservation> findByMentorIdAndReservationTime(Long mentorId, LocalDateTime time);

  // ✅ 추가: 멘토와 예약 상태로 예약 목록 조회
  List<MentoringReservation> findByMentorAndStatus(MentorUser mentor, ReservationStatus status);

  // ✅ 추가: 멘티(사용자)와 예약 상태로 예약 목록 조회
  List<MentoringReservation> findByUserAndStatus(User user, ReservationStatus status);
}
