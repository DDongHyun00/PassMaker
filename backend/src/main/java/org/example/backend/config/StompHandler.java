package org.example.backend.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.*;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.messaging.support.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
            Object userIdObj = sessionAttributes.get("userId");

            Long userId = null;
            if (userIdObj instanceof Long) {
                userId = (Long) userIdObj;
            } else if (userIdObj instanceof String) {
                try {
                    userId = Long.parseLong((String) userIdObj);
                } catch (NumberFormatException e) {
                    log.warn("STOMP 인증 실패: userId 형변환 오류");
                }
            }

            if (userId != null) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, List.of());
                accessor.setUser(authentication);
                log.info("STOMP 연결 인증 완료: userId={}", userId);
            } else {
                log.warn("STOMP 인증 실패: userId 없음");
                log.info("Session Attributes = {}", sessionAttributes);
                throw new IllegalArgumentException("유저 인증 실패");
            }
        }

        return message;
    }
}
