package org.example.backend.mentor.repository;

import org.example.backend.mentor.domain.Career;
import org.example.backend.mentor.domain.MentorUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CareerRepository extends JpaRepository<Career, Long> {
    List<Career> findByMentor(MentorUser mentor);
    void deleteByMentor(MentorUser mentor);
}
