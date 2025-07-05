package org.example.backend.auth.repository;

import org.example.backend.auth.domain.OAuthUser;
import org.example.backend.auth.domain.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OAuthUserRepository extends JpaRepository<OAuthUser, Long> {
    Optional<OAuthUser> findBySocialIdAndSocialType(String socialId, SocialType socialType);
}
