package com.focussu.backend.auth.controller;

import com.focussu.backend.auth.dto.AuthenticationRequest;
import com.focussu.backend.auth.dto.AuthenticationResponse;
import com.focussu.backend.auth.service.CustomUserDetailsService;
import com.focussu.backend.auth.service.TokenService;
import com.focussu.backend.auth.util.JwtTokenUtil;
import com.focussu.backend.common.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "사용자 인증 관련 API")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final TokenService tokenService;
    private final CustomUserDetailsService userDetailsService;

    @Operation(
            summary = "로그인",
            description = "사용자가 이메일과 비밀번호로 인증 후, JWT 토큰을 발급받아 Redis에 저장합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공 및 토큰 발급",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = AuthenticationResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "인증 실패 예시",
                                    value = """
                                            {
                                              "status": 401,
                                              "message": "아이디 또는 비밀번호가 일치하지 않습니다.",
                                              "code": "AUTH_INVALID_CREDENTIALS",
                                              "isSuccess": false
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody @Valid AuthenticationRequest loginRequest
    ) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        var userDetails = userDetailsService.loadUserByUsername(loginRequest.email());
        String token = jwtTokenUtil.generateToken(userDetails);
        tokenService.saveToken(token, userDetails.getUsername());

        return ResponseEntity.ok(new AuthenticationResponse(token));
    }


    @Operation(
            summary = "로그아웃",
            description = "JWT 토큰을 Redis에서 삭제하여 로그아웃을 진행합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "로그아웃 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "로그아웃 성공 예시",
                                    value = "\"로그아웃 성공\""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 요청 (Authorization 헤더 없음)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "로그아웃 실패 예시",
                                    value = """
                                            {
                                              "status": 400,
                                              "message": "유효하지 않은 요청입니다.",
                                              "code": "INVALID_LOGOUT_REQUEST",
                                              "isSuccess": false
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenService.removeToken(token);
            return ResponseEntity.ok("로그아웃 성공");
        }

        return ResponseEntity.badRequest().body(
                new ErrorResponse(400, "유효하지 않은 요청입니다.", "INVALID_LOGOUT_REQUEST", false)
        );
    }

}
