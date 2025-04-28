package com.focussu.backend.common.exception;

import com.focussu.backend.auth.exception.AuthException;
import com.focussu.backend.common.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        log.info(">>>>> Custom Exception", e);
        var errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getStatus())
                .body(ErrorResponse.from(errorCode));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknownException(Exception e) {
        log.info(">>>>> Global Exception", e);
        return ResponseEntity.status(500)
                .body(ErrorResponse.from(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
