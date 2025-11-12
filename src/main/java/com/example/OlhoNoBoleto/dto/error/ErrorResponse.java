package com.example.OlhoNoBoleto.dto.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String code;
    private String message;
    private String path;
    private List<FieldError> fieldErrors;
    
    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }
    
    @Data
    public static class FieldError {
        private String field;
        private String message;
        private Object rejectedValue;
        
        public FieldError(String field, String message, Object rejectedValue) {
            this.field = field;
            this.message = message;
            this.rejectedValue = rejectedValue;
        }
    }
}