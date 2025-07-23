package org.example.backend.admin.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.admin.repository.AdminUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.backend.admin.dto.AdminUserDto;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminUserController {
    private final AdminUserRepository adminUserRepository;

    // 전체 유저 목록 조회 (관리자만)
    @GetMapping("/users")
    public ResponseEntity<?> getUsers(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "") String role,
            @RequestParam(defaultValue = "가입일순") String sortOrder,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // 정렬
        Sort sort = switch (sortOrder) {
            case "이름순" -> Sort.by(Sort.Direction.ASC, "name");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
        Pageable pageable = PageRequest.of(page, size, sort);

        // 문자열 그대로 전달 (멘토/멘티)
        Boolean isMentor = null;
        if (role.equals("MENTOR")) isMentor = true;
        else if (role.equals("MENTEE")) isMentor = false;

        Page<AdminUserDto> userPage = adminUserRepository
                .searchUsersByKeyword(
                        keyword.isBlank() ? null : keyword,
                        isMentor,
                        pageable
                ).map(AdminUserDto::new);

        return ResponseEntity.ok().body(
                Map.of(
                        "content", userPage.getContent(),
                        "totalElements", userPage.getTotalElements(),
                        "totalPages", userPage.getTotalPages(),
                        "page", userPage.getNumber()
                )
        );
    }
}
