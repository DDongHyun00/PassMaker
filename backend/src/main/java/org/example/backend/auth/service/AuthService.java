package org.example.backend.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.backend.auth.dto.LoginRequestDto;
import org.example.backend.auth.dto.LoginResponseDto;
import org.example.backend.auth.dto.SignupRequestDto;

public interface AuthService {

    void signup(SignupRequestDto requestDto);

    LoginResponseDto login(LoginRequestDto requestDto, HttpServletResponse response);

    void logout(HttpServletRequest request, HttpServletResponse response);

    void reissue(HttpServletRequest request, HttpServletResponse response);

}
