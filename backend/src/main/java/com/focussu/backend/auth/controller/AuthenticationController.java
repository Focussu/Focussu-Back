package com.focussu.backend.auth.controller;

import com.focussu.backend.auth.dto.AuthenticationRequest;
import com.focussu.backend.auth.dto.AuthenticationResponse;
import com.focussu.backend.auth.service.CustomUserDetailsService;
import com.focussu.backend.auth.util.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Authentication", description = "로그인 관련 API")
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService customUserDetailsService;

    @Operation(summary = "로그인", description = "회원 로그인 시 JWT를 생성하여 반환합니다.")
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> createAuthenticationToken(
            @RequestBody AuthenticationRequest authRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.email(), authRequest.password())
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }
        // 인증 성공 후 UserDetails를 통해 JWT 생성
        final UserDetails userDetails = customUserDetailsService.loadUserByUsername(authRequest.email());
        final String jwt = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @Operation(summary = "로그아웃", description = "클라이언트 측에서 JWT를 삭제하는 방식으로 로그아웃 처리합니다.")
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // TODO: 토큰 무효화 로직
        return ResponseEntity.ok("Logout successful");
    }
}
