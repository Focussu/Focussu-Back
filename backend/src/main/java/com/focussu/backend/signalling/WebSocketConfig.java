package com.focussu.backend.signalling;

import com.focussu.backend.auth.util.JwtTokenUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final JwtTokenUtil jwtTokenUtil;
    private final SignalingHandler signalingHandler;

    public WebSocketConfig(JwtTokenUtil jwtTokenUtil, SignalingHandler signalingHandler) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.signalingHandler = signalingHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
                .addHandler(signalingHandler, "/ws/signaling")
                .setAllowedOrigins("*")
                .addInterceptors(new JwtHandshakeInterceptor(jwtTokenUtil));
    }
}
