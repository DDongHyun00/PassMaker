package org.example.backend.auth.repository;

import org.example.backend.auth.domain.Refresh;
import org.example.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshRepository extends JpaRepository<Refresh, Long> {

    // 유저로 토큰 찾기
    Optional<Refresh> findByUser(User user);

    // 토큰 문자열로 검색
    Optional<Refresh> findByToken(String token);

    // 유저 로그아웃 시 삭제용
    void deleteByUser(User user);
}