package com.focussu.backend.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.focussu.backend.auth.service.TokenService;
import com.focussu.backend.common.dto.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

import static com.focussu.backend.common.exception.ErrorCode.AUTH_INVALID_LOGOUT_REQUEST;

@Component
public class LogoutFilter extends OncePerRequestFilter {
    private final TokenService tokenService;
    private final ObjectMapper mapper = new ObjectMapper();

    public LogoutFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest req) {
        return !("POST".equals(req.getMethod()) && "/auth/logout".equals(req.getRequestURI()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    @NonNull HttpServletResponse res,
                                    @NonNull FilterChain chain)
            throws ServletException, IOException {
        String header = req.getHeader("Authorization");
        res.setCharacterEncoding("UTF-8");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            tokenService.removeToken(token);
            SecurityContextHolder.clearContext();
            res.setStatus(HttpServletResponse.SC_OK);
            res.setContentType(MediaType.APPLICATION_JSON_VALUE);
            mapper.writeValue(res.getWriter(), Map.of("message", "로그아웃 성공"));
        } else {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.setContentType(MediaType.APPLICATION_JSON_VALUE);
            mapper.writeValue(res.getWriter(),
                    ErrorResponse.from(AUTH_INVALID_LOGOUT_REQUEST));
        }
    }
}
