package org.example.backend.reservation.repository;

import org.example.backend.reservation.domain.MentoringReservation;
import org.example.backend.mentor.domain.MentorUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ReservationRepository extends JpaRepository<MentoringReservation, Long> {

  // 예약 시간 중복 체크용 메서드
  //✅ 방법 1 (객체 통째로 전달)
   boolean existsByMentorAndReservationTime(MentorUser mentor, LocalDateTime reservationTime);

  // ✅ 방법 2 (권장 - mentorId 그대로 사용 가능)
  boolean existsByMentor_IdAndReservationTime(Long mentorId, LocalDateTime reservationTime);

}
