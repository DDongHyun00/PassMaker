package org.example.backend.user.repository;

import org.example.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByNickname(String nickname);
    Optional<User> findByNameAndPhone(String name, String phone);
    Optional<User> findByEmailAndPhone(String email, String phone);
}