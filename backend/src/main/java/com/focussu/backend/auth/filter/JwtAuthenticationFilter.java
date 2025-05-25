package com.focussu.backend.auth.filter;

import com.focussu.backend.auth.exception.AuthException;
import com.focussu.backend.auth.service.CustomUserDetailsService;
import com.focussu.backend.auth.service.TokenService;
import com.focussu.backend.auth.util.JwtTokenUtil;
import com.focussu.backend.common.exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.focussu.backend.common.constant.WhiteList.isWhitelisted;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final CustomUserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final TokenService tokenService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // 1) WebSocket 핸드쉐이크는 GET + Upgrade:websocket

        // 2) 이 외에도 필요하다면 특정 경로를 추가로 제외할 수 있습니다.
        return "GET".equalsIgnoreCase(request.getMethod()) &&
                "websocket".equalsIgnoreCase(request.getHeader("Upgrade")) &&
                request.getRequestURI().equals("/ws/signaling");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    @NonNull HttpServletResponse res,
                                    @NonNull FilterChain chain)
            throws ServletException, IOException {
        String uri = req.getRequestURI();
        // 로그인·로그아웃·문서·체커 URL 은 패스
        if (uri.startsWith("/auth/") || isWhitelisted(uri)) {
            chain.doFilter(req, res);
            return;
        }

        String header = req.getHeader("Authorization");
        if (!StringUtils.hasText(header)) {
            throw new AuthException(ErrorCode.AUTH_TOKEN_MISSING);
        }
        if (!header.startsWith("Bearer ")) {
            throw new AuthException(ErrorCode.AUTH_TOKEN_MALFORMED);
        }

        String token = header.substring(7);
        String username = jwtTokenUtil.getUsernameFromToken(token);
        // 로그아웃된 토큰인지 체크
        if (!tokenService.isTokenNotRevoked(token)) {
            throw new AuthException(ErrorCode.AUTH_TOKEN_NOT_FOUND);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtTokenUtil.validateToken(token, userDetails)) {
                var auth = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        chain.doFilter(req, res);
    }
}
