package com.focussu.backend.member.dto;

import com.focussu.backend.member.model.Member;

public record MemberResponse(
        Long id,
        String name,
        String email,
        Long totalConcentrationTime,
        Long weeklyConcentrationTime,
        Long dailyConcentrationTime
) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getName(),
                member.getEmail(),
                member.getTotalConcentrationTime(),
                member.getWeeklyConcentrationTime(),
                member.getDailyConcentrationTime()
        );
    }
}
