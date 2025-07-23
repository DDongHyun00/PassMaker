package org.example.backend.admin.repository;

import org.example.backend.user.domain.Role;
import org.example.backend.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AdminUserRepository extends JpaRepository<User, Long> {
    long countByRole(Role role);

    @Query("""
    SELECT u FROM User u
    WHERE (
        :keyword IS NULL OR 
        LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
        LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
        CAST(u.id AS string) LIKE CONCAT('%', :keyword, '%')
    )
    AND (:isMentor IS NULL OR u.isMentor = :isMentor)
    """)
    Page<User> searchUsersByKeyword(@Param("keyword") String keyword,
                                    @Param("isMentor") Boolean isMentor,
                                    Pageable pageable);

    @Query("SELECT COUNT(u) FROM User u WHERE u.isMentor = true")
    long countMentors();
}
