package com.example.wellueducationservice.exception;

import org.springframework.http.HttpStatus;
import org.wellu.common.exception.BaseException;

public class QuizException extends BaseException {

    public QuizException(String message) {
        super(message, HttpStatus.CONFLICT, "QUIZ_ERROR");
    }
}
