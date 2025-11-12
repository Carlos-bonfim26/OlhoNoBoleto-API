package com.example.OlhoNoBoleto.exceptions;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends CustomException {
    public ResourceNotFoundException(String resource, String id) {
        super(resource + " não encontrado com ID: " + id, 
              HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND");
    }
    
    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND");
    }
}