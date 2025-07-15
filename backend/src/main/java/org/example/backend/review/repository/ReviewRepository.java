package org.example.backend.review.repository;

import org.example.backend.mentor.domain.MentorUser;
import org.example.backend.review.domain.Review;
import org.example.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByMentor(MentorUser mentor);

    long countByMentorId(@Param("mentorId") Long mentorId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.mentor.id = :mentorId")
    Double findAverageRatingByMentorId(@Param("mentorId") Long mentorId);

    Long id(Long id);

    List<Review> findByUser(User user);
}
