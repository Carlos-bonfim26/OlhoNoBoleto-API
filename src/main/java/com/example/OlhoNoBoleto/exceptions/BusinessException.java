package com.example.OlhoNoBoleto.exceptions;

import org.springframework.http.HttpStatus;

public class BusinessException extends CustomException{
    public BusinessException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "BUSINESS_ERROR");
    }
    
    public BusinessException(String message, String code) {
        super(message, HttpStatus.BAD_REQUEST, code);
    }
}
