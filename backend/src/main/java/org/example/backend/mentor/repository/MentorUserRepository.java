package org.example.backend.mentor.repository;

import org.example.backend.mentor.domain.MentorUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentorUserRepository extends JpaRepository<MentorUser, Long> {
  // 현재는 기본 findById()만 필요함
}
