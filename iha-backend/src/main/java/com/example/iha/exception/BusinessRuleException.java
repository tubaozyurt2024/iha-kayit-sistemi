package com.example.iha.exception;

/**
 * İş kuralı ihlallerinde fırlatılır; 400 Bad Request olarak döner.
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
