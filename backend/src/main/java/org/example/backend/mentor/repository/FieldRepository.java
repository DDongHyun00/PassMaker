package org.example.backend.mentor.repository;

import org.example.backend.mentor.domain.Field;
import org.example.backend.mentor.domain.MentorUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FieldRepository extends JpaRepository<Field, Long> {
    List<Field> findByMentor(MentorUser mentor);
    void deleteByMentor(MentorUser mentor);
}
