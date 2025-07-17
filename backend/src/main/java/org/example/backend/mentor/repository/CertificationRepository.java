package org.example.backend.mentor.repository;

import org.example.backend.mentor.domain.Certification;
import org.example.backend.mentor.domain.MentorUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CertificationRepository extends JpaRepository<Certification, Long> {
    List<Certification> findByMentor(MentorUser mentor);
    void deleteByMentor(MentorUser mentor);
}
