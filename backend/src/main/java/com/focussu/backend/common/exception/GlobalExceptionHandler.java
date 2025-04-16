package com.focussu.backend.common.exception;

import com.focussu.backend.common.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        var errorCode = e.getErrorCode();
        e.printStackTrace();
        return ResponseEntity.status(errorCode.getStatus())
                .body(ErrorResponse.from(errorCode));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknownException(Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500)
                .body(ErrorResponse.from(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
