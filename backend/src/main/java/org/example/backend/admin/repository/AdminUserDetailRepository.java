package org.example.backend.admin.repository;

import org.example.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminUserDetailRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.mentorReservations r " +
            "LEFT JOIN FETCH r.mentor m " +
            "LEFT JOIN FETCH m.user " +
            "LEFT JOIN FETCH r.payment " +
            "WHERE u.id = :id")

    Optional<User> findByIdWithReservations(@Param("id") Long id);
}
