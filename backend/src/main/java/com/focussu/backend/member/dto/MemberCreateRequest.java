package com.focussu.backend.member.dto;

import com.focussu.backend.member.model.Member;


public record MemberCreateRequest(String name, String email, String password) {

    public Member toEntity(String compressedPassword) {
        return Member.builder()
                .name(name)
                .email(email)
                .password(compressedPassword)
                .build();
    }
}
