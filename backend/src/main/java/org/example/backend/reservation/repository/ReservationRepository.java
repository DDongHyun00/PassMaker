package org.example.backend.reservation.repository;

import org.example.backend.reservation.domain.MentoringReservation;
import org.example.backend.mentor.domain.MentorUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ReservationRepository extends JpaRepository<MentoringReservation, Long> {

  // 예약 시간 중복 체크용 메서드
  boolean existsByMentorAndReservationTime(MentorUser mentor, LocalDateTime reservationTime);
  boolean existsByMentorIdAndReservationTime(Long mentorId, LocalDateTime time); // ✅ 추가

}
