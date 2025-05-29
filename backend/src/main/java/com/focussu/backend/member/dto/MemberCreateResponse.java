package com.focussu.backend.member.dto;

import com.focussu.backend.member.model.Member;
import io.swagger.v3.oas.annotations.media.Schema;

public record MemberCreateResponse(

        @Schema(description = "회원 ID", example = "1")
        Long id,

        @Schema(description = "회원 이름", example = "oxdjww")
        String name,

        @Schema(description = "회원 이메일", example = "test@gmail.com")
        String email,

        @Schema(description = "회원 자기소개", example = "My name is seungmin")
        String description,

        @Schema(description = "회원 이미지", example = "https://sample.url/image")
        String profileImageUrl

) {
    public static MemberCreateResponse from(Member member) {
        return new MemberCreateResponse(
                member.getId(),
                member.getName(),
                member.getEmail(),
                member.getDescription(),
                member.getProfileImageUrl()
        );
    }
}
