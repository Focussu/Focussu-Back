package com.focussu.backend.signalling;

import com.focussu.backend.auth.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Slf4j
public class JwtHandshakeInterceptor implements HandshakeInterceptor {
    private final JwtTokenUtil jwtTokenUtil;

    public JwtHandshakeInterceptor(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler handler,
                                   Map<String, Object> attrs) throws Exception {
        // 1) 헤더에서 Authorization 꺼내기
        List<String> authHeaders = request.getHeaders().get(HttpHeaders.AUTHORIZATION);
        String token = null;
        if (authHeaders != null && !authHeaders.isEmpty()
                && authHeaders.get(0).startsWith("Bearer ")) {
            token = authHeaders.get(0).substring(7);
        }

        // 2) 헤더에 토큰이 없으면 query param 으로 fallback
        if (token == null) {
            token = UriComponentsBuilder.fromUri(request.getURI())
                    .build()
                    .getQueryParams()
                    .getFirst("token");
        }

        // 3) 로깅
        log.info("[HandshakeInterceptor] 요청 URI: {}", request.getURI());
        log.info("[HandshakeInterceptor] 토큰: {}", token);

        // 4) 검증
        if (token == null || !jwtTokenUtil.validateToken(token)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        attrs.put("userId", jwtTokenUtil.getUsernameFromToken(token));
        return true;
    }


    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        // 아무 처리 필요 없으면 빈 바디로 둡니다.
        // // 또는 디버깅용:
        System.out.println("WebSocket Handshake 완료: " + request.getRemoteAddress());
    }

}
