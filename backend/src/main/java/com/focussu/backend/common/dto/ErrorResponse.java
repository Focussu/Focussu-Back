package com.focussu.backend.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ErrorResponse(
        @Schema(description = "HTTP 상태 코드", example = "404")
        int status,
        @Schema(description = "에러 메시지", example = "회원을 찾을 수 없습니다.")
        String message,
        @Schema(description = "에러 코드", example = "MEMBER_NOT_FOUND")
        String code,
        @Schema(description = "성공 여부", example = "false")
        boolean isSuccess
) {
    public static ErrorResponse from(com.focussu.backend.common.exception.ErrorCode errorCode) {
        return new ErrorResponse(
                errorCode.getStatus(),
                errorCode.getMessage(),
                errorCode.name(),
                false
        );
    }
}
