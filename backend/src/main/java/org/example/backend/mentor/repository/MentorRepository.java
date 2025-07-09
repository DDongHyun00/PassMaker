
package org.example.backend.mentor.repository;

import org.example.backend.mentor.domain.MentorUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MentorRepository extends JpaRepository<MentorUser, Long> {
    //멘토 엔티티를 조회하기 위한 jpaRepository를 상속받는 인터페이스.
}
