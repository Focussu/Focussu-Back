package com.focussu.backend.member.exception;

import com.focussu.backend.common.exception.CustomException;
import com.focussu.backend.common.exception.ErrorCode;

public class MemberException extends CustomException {
    public MemberException(ErrorCode errorCode) {
        super(errorCode);
    }
}
