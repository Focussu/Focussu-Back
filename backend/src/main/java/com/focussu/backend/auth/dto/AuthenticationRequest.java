package com.focussu.backend.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

public record AuthenticationRequest(

        @Schema(
                description = "사용자 이메일",
                example = "test@gmail.com",
                maxLength = 50
        )
        @Pattern(regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])+[.][a-zA-Z]{2,3}$", message = "이메일 주소 양식을 확인해주세요")
        String email,

        @Schema(
                description = "사용자 비밀번호",
                example = "testpassword",
                minLength = 8,
                maxLength = 20
        )
        String password

) {
}
