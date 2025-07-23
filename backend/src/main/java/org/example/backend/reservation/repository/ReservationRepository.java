package org.example.backend.reservation.repository;

import org.example.backend.reservation.domain.MentoringReservation;
import org.example.backend.mentor.domain.MentorUser;
import org.example.backend.reservation.domain.ReservationStatus;
import org.example.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<MentoringReservation, Long> {

  // 예약 시간 중복 체크용 메서드
  // ✅ 방법 2 (권장 - mentorId 그대로 사용 가능)
  boolean existsByMentor_IdAndReservationTime(Long mentorId, LocalDateTime reservationTime);

  boolean existsByMentorAndReservationTime(MentorUser mentor, LocalDateTime reservationTime);
  List<MentoringReservation> findByMentor(MentorUser mentor);
  List<MentoringReservation> findByUser(User user);
  List<MentoringReservation> findByUserAndStatus(User user, ReservationStatus status);
  List<MentoringReservation> findByMentorAndStatusIn(MentorUser mentor, List<ReservationStatus> statuses);
  // ✅ 추가: 사용자가 멘티로서 예약한 내역을 상태 리스트로 조회 (멘토/멘티 구분 없이 사용)
  List<MentoringReservation> findByUserAndStatusIn(User user, List<ReservationStatus> statuses);

}
