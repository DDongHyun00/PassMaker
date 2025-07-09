package org.example.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import java.security.Principal;

import java.util.Map;

@RequiredArgsConstructor
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketHandshakeInterceptor handshakeInterceptor;
    private final StompHandler stompHandler;

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        registry.addEndpoint("/ws") // 클라이언트 연결 주소
                .setHandshakeHandler(handshakeHandler())
                .addInterceptors(handshakeInterceptor) // 인터셉터 반드시 등록
                .setAllowedOriginPatterns("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic"); // 서버 → 클라이언트
        registry.setApplicationDestinationPrefixes("/app"); // 클라이언트 → 서버
    }

    @Bean
    public DefaultHandshakeHandler handshakeHandler() {
        return new DefaultHandshakeHandler() {
            @Override
            protected Principal determineUser(ServerHttpRequest request,
                                              WebSocketHandler wsHandler,
                                              Map<String, Object> attributes) {
                Object userId = attributes.get("userId");
                if (userId != null) {
                    return () -> String.valueOf(userId);
                }
                return null;
            }
        };
    }

}
