package com.example.OlhoNoBoleto.handlers;

import com.example.OlhoNoBoleto.dto.error.ErrorResponse;
import com.example.OlhoNoBoleto.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Custom Exceptions
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex, HttpServletRequest request) {
        log.error("Custom Exception: {}", ex.getMessage(), ex);
        
        ErrorResponse error = new ErrorResponse();
        error.setStatus(ex.getStatus().value());
        error.setError(ex.getStatus().getReasonPhrase());
        error.setCode(ex.getStatusCode());
        error.setMessage(ex.getMessage());
        error.setPath(request.getRequestURI());
        
        return new ResponseEntity<>(error, ex.getStatus());
    }

    // Validation Errors (Bean Validation)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.error("Validation error: {}", ex.getMessage());
        
        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ErrorResponse.FieldError(
                        error.getField(),
                        error.getDefaultMessage(),
                        error.getRejectedValue()))
                .collect(Collectors.toList());
        
        ErrorResponse error = new ErrorResponse();
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
        error.setCode("VALIDATION_ERROR");
        error.setMessage("Erro de validação nos campos");
        error.setPath(request.getRequestURI());
        error.setFieldErrors(fieldErrors);
        
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // External API Errors
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorResponse> handleHttpClientErrorException(
            HttpClientErrorException ex, HttpServletRequest request) {
        log.error("External API error: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse();
        error.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
        error.setError("Service Unavailable");
        error.setCode("EXTERNAL_API_ERROR");
        error.setMessage("Erro ao consultar serviço externo: " + ex.getMessage());
        error.setPath(request.getRequestURI());
        
        return new ResponseEntity<>(error, HttpStatus.SERVICE_UNAVAILABLE);
    }

    // Timeout Errors
    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ErrorResponse> handleTimeoutException(
            ResourceAccessException ex, HttpServletRequest request) {
        log.error("Timeout error: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse();
        error.setStatus(HttpStatus.REQUEST_TIMEOUT.value());
        error.setError("Request Timeout");
        error.setCode("TIMEOUT_ERROR");
        error.setMessage("Tempo limite excedido na requisição");
        error.setPath(request.getRequestURI());
        
        return new ResponseEntity<>(error, HttpStatus.REQUEST_TIMEOUT);
    }

    // Generic Exception (Fallback)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        
        ErrorResponse error = new ErrorResponse();
        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.setError("Internal Server Error");
        error.setCode("INTERNAL_ERROR");
        error.setMessage("Erro interno no servidor");
        error.setPath(request.getRequestURI());
        
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}