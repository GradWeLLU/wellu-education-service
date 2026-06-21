package com.example.wellueducationservice.exception;

import org.wellu.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CsvParsingException extends BaseException {

    public CsvParsingException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "CSV_PARSE_ERROR");
    }
}