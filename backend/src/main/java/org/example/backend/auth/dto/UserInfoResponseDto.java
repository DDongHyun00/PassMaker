package org.example.backend.auth.dto;

import org.example.backend.user.domain.Role;

public record UserInfoResponseDto(Long userId, String username, boolean mentor, Role role) {}
