package org.example.backend.review.repository;

import org.example.backend.mentor.domain.MentorUser;
import org.example.backend.review.domain.Review;
import org.example.backend.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByReservation_Mentor(MentorUser mentor);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.reservation.mentor.id = :mentorId")
    long countByMentorId(@Param("mentorId") Long mentorId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.reservation.mentor.id = :mentorId")
    Double findAverageRatingByMentorId(@Param("mentorId") Long mentorId);

    List<Review> findByReservation_User_Id(Long userId);
}