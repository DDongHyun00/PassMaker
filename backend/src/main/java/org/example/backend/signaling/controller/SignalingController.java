package org.example.backend.signaling.controller;

import org.example.backend.signaling.dto.SignalMessageDTO;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.security.Principal;
import java.util.Map;

@Controller
public class SignalingController {

    private final SimpMessagingTemplate messagingTemplate;

    public SignalingController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/signal") // 클라이언트 → /app/signal
    public void handleSignal(
            @Payload SignalMessageDTO message,
            @Header("simpSessionAttributes") Map<String, Object> attributes
    ) {
        // 1. WebSocket 세션에서 userId 꺼내기
        Long userId = (Long) attributes.get("userId"); //
        if (userId == null) {
            System.out.println("Unauthorized WebSocket message blocked.");
            return;
        }

        // 2. 메시지에 sender 강제 주입 (위조 방지)
        message.setSender(String.valueOf(userId));

        // 3. 구독자들에게 전달
        messagingTemplate.convertAndSend(
                "/topic/room/" + message.getRoomId(),
                message
        );
    }
}
