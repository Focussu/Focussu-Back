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
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;

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
        String username = auth.getName();
        // 반드시 UserDetails 로드해서 토큰 생성
        UserDetails user = userDetailsService.loadUserByUsername(username);
        String jwt = jwtTokenUtil.generateToken(user);
        tokenService.saveToken(jwt, user.getUsername());
        res.setCharacterEncoding("UTF-8");
        res.addHeader("Authorization", "Bearer " + jwt);
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        mapper.writeValue(res.getWriter(), new AuthenticationResponse(jwt));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest req,
                                              HttpServletResponse res,
                                              AuthenticationException failed)
            throws IOException, ServletException {
        // 인증 실패 응답
        res.setCharacterEncoding("UTF-8");
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        mapper.writeValue(res.getWriter(),
                ErrorResponse.from(failed instanceof AuthenticationServiceException
                        ? ErrorCode.AUTH_INVALID_CREDENTIALS
                        : ErrorCode.INTERNAL_SERVER_ERROR
                )
        );
    }
}
