package com.focussu.backend.test;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/test")
@Tag(name = "Test", description = "보안 및 상태 확인용 테스트 API")
public class TestController {

    @GetMapping("/security-check")
    @Operation(summary = "JWT 인증 체크", description = "JWT 토큰이 올바르게 작동하는지 확인하는 엔드포인트입니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "인증 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "인증 성공 예시",
                                    value = """
                                            {
                                              "message": "AUTHENTICATION SUCCESS"
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<Map<String, String>> checkSecurity() {
        return ResponseEntity.ok(Map.of("message", "AUTHENTICATION SUCCESS"));
    }

    @GetMapping("/health-check")
    @Operation(summary = "헬스 체크", description = "서버가 정상 작동 중인지 확인하는 공개 엔드포인트입니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "서버 정상 작동",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "헬스 체크 성공 예시",
                                    value = """
                                            {
                                              "message": "SERVER IS HEALTHY!!!"
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<Map<String, String>> checkHealth() {
        return ResponseEntity.ok(Map.of("message", "SERVER IS HEALTHY!!!"));
    }
}
