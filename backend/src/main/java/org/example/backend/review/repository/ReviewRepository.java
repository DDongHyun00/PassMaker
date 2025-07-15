package org.example.backend.review.repository;

import org.example.backend.mentor.domain.MentorUser;
import org.example.backend.review.domain.Review;
<<<<<<< Updated upstream
import org.example.backend.user.domain.User;
=======
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
>>>>>>> Stashed changes
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByMentor(MentorUser mentor);

    long countByMentorId(@Param("mentorId") Long mentorId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.mentor.id = :mentorId")
    Double findAverageRatingByMentorId(@Param("mentorId") Long mentorId);

<<<<<<< Updated upstream
    Long id(Long id);

    List<Review> findByUser(User user);
=======
    Page<Review> findAllByIdIn(List<Long> ids, Pageable pageable);
>>>>>>> Stashed changes
}
