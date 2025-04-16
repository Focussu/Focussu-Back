package com.focussu.backend.auth.exception;

import com.focussu.backend.common.exception.CustomException;
import com.focussu.backend.common.exception.ErrorCode;

public class AuthException extends CustomException {
    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }
}
