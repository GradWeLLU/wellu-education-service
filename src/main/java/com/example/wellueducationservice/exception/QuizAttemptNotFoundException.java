package com.example.wellueducationservice.exception;

import org.springframework.http.HttpStatus;
import org.wellu.common.exception.BaseException;

public class QuizAttemptNotFoundException extends BaseException {

    public QuizAttemptNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "QUIZ_ATTEMPT_NOT_FOUND");
    }
}
