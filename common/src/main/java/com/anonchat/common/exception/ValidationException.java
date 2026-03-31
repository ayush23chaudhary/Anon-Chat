package com.anonchat.common.exception;

/**
 * Exception thrown when validation fails.
 */
public class ValidationException extends RuntimeException {
    private final String errorCode;

    public ValidationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ValidationException(String message) {
        this(message, "VALIDATION_ERROR");
    }

    public String getErrorCode() {
        return errorCode;
    }
}
