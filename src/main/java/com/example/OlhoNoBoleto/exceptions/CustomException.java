package com.example.OlhoNoBoleto.exceptions;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final HttpStatus status;
    private final String statusCode;

    public CustomException(String message, HttpStatus status, String statusCode) {
        super(message);
        this.status = status;
        this.statusCode = statusCode;
    }

}
