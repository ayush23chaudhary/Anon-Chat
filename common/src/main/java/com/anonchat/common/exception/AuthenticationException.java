package com.anonchat.common.exception;

/**
 * Exception thrown when authentication fails.
 * Never leaks sensitive information in messages.
 */
public class AuthenticationException extends RuntimeException {
    private final String errorCode;

    public AuthenticationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public AuthenticationException(String message) {
        this(message, "AUTH_ERROR");
    }

    public String getErrorCode() {
        return errorCode;
    }
}
