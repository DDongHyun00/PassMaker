
package org.example.backend.mentor.repository;

import org.example.backend.mentor.domain.MentorUser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MentorRepository extends JpaRepository<MentorUser, Long> {

    @EntityGraph(attributePaths = "user") // 강제로 user 같이 가져오도록 지정
    @Query("SELECT m FROM MentorUser m") // 명시적 쿼리 사용
    List<MentorUser> findAllWithUser();           // 이름은 그대로 유지해도 됨
}
