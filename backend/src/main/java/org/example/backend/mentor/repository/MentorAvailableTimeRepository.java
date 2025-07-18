package org.example.backend.mentor.repository;

import org.example.backend.mentor.domain.MentorAvailableTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 멘토의 가용 시간(MentorAvailableTime) 엔티티에 대한 데이터 접근을 담당하는 Repository 인터페이스입니다.
 * Spring Data JPA의 JpaRepository를 상속받아 기본적인 CRUD 기능을 제공합니다.
 */
@Repository
public interface MentorAvailableTimeRepository extends JpaRepository<MentorAvailableTime, Long> {
    /**
     * 특정 멘토의 모든 가용 시간 목록을 조회합니다.
     * @param mentorId 멘토의 고유 ID
     * @return 해당 멘토의 가용 시간 목록
     */
    List<MentorAvailableTime> findByMentorId(Long mentorId);

    /**
     * 특정 멘토의 특정 요일에 해당하는 가용 시간 목록을 조회합니다.
     * @param mentorId 멘토의 고유 ID
     * @param dayOfWeek 조회할 요일 (DayOfWeek Enum)
     * @return 해당 멘토의 특정 요일 가용 시간 목록
     */
    List<MentorAvailableTime> findByMentorIdAndDayOfWeek(Long mentorId, java.time.DayOfWeek dayOfWeek);

    /**
     * 특정 멘토의 모든 가용 시간을 삭제합니다.
     * 주로 멘토가 가용 시간을 재설정할 때 기존 데이터를 일괄 삭제하는 용도로 사용됩니다.
     * @param mentorId 삭제할 가용 시간의 멘토 ID
     */
    void deleteByMentorId(Long mentorId);
}