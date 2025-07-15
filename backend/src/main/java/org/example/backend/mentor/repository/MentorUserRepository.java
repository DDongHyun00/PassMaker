package org.example.backend.mentor.repository;

import org.example.backend.mentor.domain.MentorUser;
import org.example.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional; // Optional 클래스 임포트 추가

@Repository
public interface MentorUserRepository extends JpaRepository<MentorUser, Long> {
    Optional<MentorUser> findByUser(User user);
}