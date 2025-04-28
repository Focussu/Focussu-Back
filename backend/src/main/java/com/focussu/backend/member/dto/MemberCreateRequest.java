package com.focussu.backend.member.dto;

import com.focussu.backend.member.model.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

public record MemberCreateRequest(

        @Schema(
                description = "회원 이름",
                example = "test",
                maxLength = 30
        )
        String name,

        @Schema(
                description = "이메일 주소 (형식: user@example.com)",
                example = "test@gmail.com",
                maxLength = 50
        )
        @Pattern(regexp="^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])+[.][a-zA-Z]{2,3}$", message="이메일 주소 양식을 확인해주세요")
        String email,

        @Schema(
                description = "비밀번호 (8~20자, 영문/숫자/특수문자 조합 권장)",
                example = "testpassword",
                minLength = 8,
                maxLength = 20
        )
        String password

) {
    public Member toEntity(String compressedPassword) {
        return Member.builder()
                .name(name)
                .email(email)
                .password(compressedPassword)
                .build();
    }
}
