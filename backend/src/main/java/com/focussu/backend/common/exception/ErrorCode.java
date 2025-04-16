package com.focussu.backend.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // 공통
    INTERNAL_SERVER_ERROR(500, "서버 내부 에러"),
    INVALID_INPUT(400, "잘못된 입력입니다"),

    // Auth 관련 에러
    AUTH_INVALID_CREDENTIALS(401, "아이디 또는 비밀번호가 일치하지 않습니다."),
    AUTH_TOKEN_MISSING(401, "Authorization 헤더가 존재하지 않습니다."),
    AUTH_TOKEN_MALFORMED(401, "토큰 형식이 올바르지 않습니다."),
    AUTH_TOKEN_EXPIRED(401, "토큰이 만료되었습니다."),
    AUTH_TOKEN_INVALID_SIGNATURE(401, "토큰 서명이 유효하지 않습니다."),
    AUTH_TOKEN_NOT_FOUND(401, "로그아웃된 토큰입니다."),

    // Member 관련 에러
    MEMBER_NOT_FOUND(404, "회원을 찾을 수 없습니다."),
    MEMBER_ALREADY_EXISTS(409, "이미 존재하는 이메일입니다.");

    private final int status;
    private final String message;

    ErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
