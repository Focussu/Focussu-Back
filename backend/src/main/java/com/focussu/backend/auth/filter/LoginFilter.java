package com.focussu.backend.auth.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.focussu.backend.auth.dto.AuthenticationRequest;
import com.focussu.backend.auth.dto.AuthenticationResponse;
import com.focussu.backend.auth.service.CustomUserDetailsService;
import com.focussu.backend.auth.service.TokenService;
import com.focussu.backend.auth.util.JwtTokenUtil;
import com.focussu.backend.common.dto.ErrorResponse;
import com.focussu.backend.common.exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final ObjectMapper mapper = new ObjectMapper();
    private final CustomUserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final TokenService tokenService;

    public LoginFilter(CustomUserDetailsService uds,
                       JwtTokenUtil jwtUtil,
                       TokenService tokenSvc) {
        this.userDetailsService = uds;
        this.jwtTokenUtil = jwtUtil;
        this.tokenService = tokenSvc;
        setFilterProcessesUrl("/auth/login");        // 로그인 엔드포인트
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res)
            throws AuthenticationException {
        try {
            AuthenticationRequest creds =
                    mapper.readValue(req.getInputStream(), AuthenticationRequest.class);
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            creds.email(), creds.password());
            return getAuthenticationManager().authenticate(authToken);
        } catch (IOException e) {
            throw new AuthenticationServiceException("Invalid login payload", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth)
            throws IOException, ServletException {

        // 1. 인증 성공 후 사용자 정보 가져오기
        String username = auth.getName();
        UserDetails user = userDetailsService.loadUserByUsername(username);
        log.info("[LOGIN FILTER] {} 로그인 성공..", username);

        // 2. 새로운 JWT 토큰 생성
        String newJwt = jwtTokenUtil.generateToken(user);
        log.info("[LOGIN FILTER] {} 토큰 생성 성공..", username);

        // 3. (추가) Redis에서 해당 사용자의 기존 토큰 삭제
        tokenService.removeTokenByUsername(user.getUsername());
        log.info("[LOGIN FILTER] {} 기존 토큰 삭제..", username);

        // 4. 새로운 토큰을 Redis에 저장
        tokenService.saveToken(newJwt, user.getUsername());
        log.info("[LOGIN FILTER] {} 새 토큰 저장..", user.getUsername());

        // 5. 응답 설정
        res.setCharacterEncoding("UTF-8");
        res.addHeader("Authorization", "Bearer " + newJwt);
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        mapper.writeValue(res.getWriter(), new AuthenticationResponse(newJwt));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest req,
                                              HttpServletResponse res,
                                              AuthenticationException failed)
            throws IOException, ServletException {
        // 인증 실패 응답
        res.setCharacterEncoding("UTF-8");
        ErrorCode code;
        int status;
        if (failed instanceof BadCredentialsException) {
            code = ErrorCode.AUTH_INVALID_CREDENTIALS;
            status = HttpServletResponse.SC_UNAUTHORIZED;
        } else if (failed instanceof AuthenticationServiceException) {
            code = ErrorCode.INVALID_INPUT;
            status = HttpServletResponse.SC_BAD_REQUEST;
        } else {
            code = ErrorCode.INTERNAL_SERVER_ERROR;
            status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        }
        res.setStatus(status);
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        mapper.writeValue(res.getWriter(), ErrorResponse.from(code));
    }
}
