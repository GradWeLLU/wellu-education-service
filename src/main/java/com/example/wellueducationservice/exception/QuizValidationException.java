package com.example.wellueducationservice.exception;

import org.springframework.http.HttpStatus;
import org.wellu.common.exception.BaseException;

public class QuizValidationException extends BaseException {

    public QuizValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "QUIZ_VALIDATION_ERROR");
    }
}
