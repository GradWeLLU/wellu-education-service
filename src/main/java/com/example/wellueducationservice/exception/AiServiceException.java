package com.example.wellueducationservice.exception;

import org.springframework.http.HttpStatus;
import org.wellu.common.exception.BaseException;


public class AiServiceException extends BaseException {
    public AiServiceException(String message) {
        super(message, HttpStatus.BAD_GATEWAY, "AI_SERVICE_ERROR");
    }
}
