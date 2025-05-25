package com.focussu.backend.signalling;

import com.focussu.backend.auth.util.JwtTokenUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final JwtTokenUtil jwtTokenUtil;  // 기존에 쓰시던 유틸

    public WebSocketConfig(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
                .addHandler(signalingHandler(), "/ws/signaling")
                .setAllowedOrigins("*")
                .addInterceptors(new JwtHandshakeInterceptor(jwtTokenUtil));
    }

    @Bean
    public WebSocketHandler signalingHandler() {
        return new SignalingHandler();
    }
}
