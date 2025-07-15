package org.example.backend.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.user.domain.User;
import org.example.backend.auth.dto.UserResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.example.backend.user.service.UserService;
import org.example.backend.reservation.dto.ReservationDto;

import java.util.List;

// @Controller	- HTML 페이지(view) 반환
// @RestController	- 데이터(JSON) 반환
// @AuthenticationPrincipal - 현재 로그인한 사용자의 정보를 가져오는 어노테이션
// ResponseEntity<?> - Http 응답 전체를 컨트롤 할 수 있는 Spring의 반환 객체 (상태코드, 응답, 헤더, 본문 전부 직접 설정 가능)

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users") // URI를 /api/users로 변경
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> getMyInfo(@AuthenticationPrincipal User user){

        if(user == null){
            return ResponseEntity.status(401).body("인증되지 않은 사용자입니다.");
        }

        return ResponseEntity.ok(new UserResponseDto(user));
    }

    @GetMapping("/me/reservations")
    public ResponseEntity<List<ReservationDto>> getMyReservations(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<ReservationDto> reservations = userService.getMyReservations(user.getId());
        return ResponseEntity.ok(reservations);
    }
}
