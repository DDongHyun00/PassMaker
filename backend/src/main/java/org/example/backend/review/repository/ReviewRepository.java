package org.example.backend.review.repository;

import org.example.backend.review.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT COUNT(r) FROM Review r WHERE r.mentor.mentorId = :mentorId")
    long countByMentorId(@Param("mentorId") Long mentorId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.mentor.mentorId = :mentorId")
    Double findAverageRatingByMentorId(@Param("mentorId") Long mentorId);
}
