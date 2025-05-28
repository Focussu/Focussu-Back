package com.focussu.backend.studyroom.exception;


import com.focussu.backend.common.exception.CustomException;
import com.focussu.backend.common.exception.ErrorCode;

public class StudyRoomException extends CustomException {
    public StudyRoomException(ErrorCode errorCode) {
        super(errorCode);
    }
}
