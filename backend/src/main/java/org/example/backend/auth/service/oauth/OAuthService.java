package org.example.backend.auth.service.oauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.backend.auth.domain.SocialType;

public interface OAuthService {
    void login(String accessToken, HttpServletResponse response) throws Exception;
    void logout(HttpServletRequest request, HttpServletResponse response);
    void reissue(HttpServletRequest request, HttpServletResponse response);
    SocialType getSocialType();


}
