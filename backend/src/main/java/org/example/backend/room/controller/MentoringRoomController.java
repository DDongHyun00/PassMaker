package org.example.backend.room.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.backend.auth.domain.CustomUserDetails;
import org.example.backend.room.dto.MentoringRoomEnterResponseDTO;
import org.example.backend.room.dto.RoomCodeRequestDTO;
import org.example.backend.room.service.MentoringRoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class MentoringRoomController {

    private final MentoringRoomService mentoringRoomService;

    @PostMapping("/{roomId}/enter")
    public ResponseEntity<MentoringRoomEnterResponseDTO> enterRoom(
            @PathVariable Long roomId,
            @RequestBody RoomCodeRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        String username = userDetails.getUsername();
        MentoringRoomEnterResponseDTO response = mentoringRoomService.enterRoom(roomId, request.getRoomCode(), username);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/exit")
    public ResponseEntity<?> exitRoom(@RequestParam Long roomId) {
        mentoringRoomService.closeRoom(roomId); // 내부에서 summarize 호출
        return ResponseEntity.ok("방 종료 및 요약 완료");
    }
}

