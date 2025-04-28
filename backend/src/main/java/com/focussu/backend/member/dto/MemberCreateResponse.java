package com.focussu.backend.member.dto;

import com.focussu.backend.member.model.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

public record MemberCreateResponse(

        @Schema(description = "회원 ID", example = "1")
        Long id,

        @Schema(description = "회원 이름", example = "oxdjww")
        String name,

        @Schema(description = "회원 이메일", example = "test@gmail.com")
        String email

) {
    public static MemberCreateResponse from(Member member) {
        return new MemberCreateResponse(
                member.getId(),
                member.getName(),
                member.getEmail()
        );
    }
}
