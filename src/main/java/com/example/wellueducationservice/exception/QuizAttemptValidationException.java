package com.example.wellueducationservice.exception;

import org.springframework.http.HttpStatus;
import org.wellu.common.exception.BaseException;

public class QuizAttemptValidationException extends BaseException {

    public QuizAttemptValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "QUIZ_ATTEMPT_VALIDATION_ERROR");
    }
}
