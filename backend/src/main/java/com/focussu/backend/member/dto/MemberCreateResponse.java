package com.focussu.backend.member.dto;

import com.focussu.backend.member.model.Member;

public record MemberCreateResponse(
        Long id,
        String name,
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
