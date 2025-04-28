package com.focussu.backend.auth.controller;

import com.focussu.backend.auth.dto.AuthenticationRequest;
import com.focussu.backend.auth.dto.AuthenticationResponse;
import com.focussu.backend.common.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "필터 기반 로그인·로그아웃 명세")
public class AuthDocumentController {

    @Operation(
            summary = "로그인",
            description = "로그인 요청을 처리하는 필터(LoginFilter)에서 동작합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공 및 토큰 발급",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AuthenticationResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답 예시",
                                    value = """
                                            {
                                              "token": "eyJhbGciOiJIUzI1NiIsInR5..."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "실패 응답 예시",
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
    public void login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "이메일·비밀번호 로그인 요청",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AuthenticationRequest.class),
                            examples = @ExampleObject(
                                    name = "요청 예시",
                                    value = """
                                            {
                                              "email": "user@example.com",
                                              "password": "password123"
                                            }
                                            """
                            )
                    )
            )
            @RequestBody AuthenticationRequest request
    ) {
        // 구현 없음 — 문서화 전용
    }

    @Operation(
            summary = "로그아웃",
            description = "로그아웃 요청을 처리하는 필터(LogoutFilter)에서 동작합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "로그아웃 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    name = "성공 응답 예시",
                                    value = """
                                            {
                                              "message": "로그아웃 성공"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 로그아웃 요청",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "실패 응답 예시",
                                    value = """
                                            {
                                              "status": 400,
                                              "message": "유효하지 않은 요청입니다.",
                                              "code": "AUTH_INVALID_LOGOUT_REQUEST",
                                              "isSuccess": false
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/logout")
    public void logout() {
        // 구현 없음 — 문서화 전용
    }
}
