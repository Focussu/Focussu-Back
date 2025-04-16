package com.focussu.backend.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.focussu.backend.auth.exception.AuthException;
import com.focussu.backend.auth.service.CustomUserDetailsService;
import com.focussu.backend.auth.service.TokenService;
import com.focussu.backend.auth.util.JwtTokenUtil;
import com.focussu.backend.common.constant.WhiteList;
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
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

import static com.focussu.backend.common.constant.WhiteList.isWhitelisted;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain) throws ServletException, IOException {
        String requestUri = request.getRequestURI();

        // whitelist에 해당하면 토큰 검사를 건너뛰도록 함
        if (isWhitelisted(requestUri)) {
            chain.doFilter(request, response);
            return;
        }
        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        if (!StringUtils.hasText(authHeader)) {
            // 토큰 미제공 시 예외 발생 (여기서 JwtExceptionFilter가 처리)
            throw new AuthException(ErrorCode.AUTH_TOKEN_MISSING);
        }
        if (!authHeader.startsWith("Bearer ")) {
            throw new AuthException(ErrorCode.AUTH_TOKEN_MALFORMED);
        }
        jwt = authHeader.substring(7);

        // 파싱하며 토큰 유효성 검증 (예: 만료, 서명 오류 등)
        username = jwtTokenUtil.getUsernameFromToken(jwt);

        // 로그아웃 토큰
        if (!tokenService.isTokenRevoked(jwt)) {
            throw new AuthException(ErrorCode.AUTH_TOKEN_NOT_FOUND);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtTokenUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        chain.doFilter(request, response);
    }
}
