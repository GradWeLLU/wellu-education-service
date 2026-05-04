package com.example.wellueducationservice.exception;

import org.springframework.http.HttpStatus;
import org.wellu.common.exception.BaseException;

public class FactNotFoundException extends BaseException {
    public FactNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "FACT_NOT_FOUND");
    }
}