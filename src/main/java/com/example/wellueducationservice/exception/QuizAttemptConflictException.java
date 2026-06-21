package com.example.wellueducationservice.exception;

import org.springframework.http.HttpStatus;
import org.wellu.common.exception.BaseException;

public class QuizAttemptConflictException extends BaseException {

    public QuizAttemptConflictException(String message) {
        super(message, HttpStatus.CONFLICT, "QUIZ_ATTEMPT_CONFLICT");
    }
}
