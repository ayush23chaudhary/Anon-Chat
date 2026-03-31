package com.anonchat.common.exception;

/**
 * Exception thrown when a requested resource is not found.
 */
public class ResourceNotFoundException extends RuntimeException {
    private final String errorCode;

    public ResourceNotFoundException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ResourceNotFoundException(String message) {
        this(message, "RESOURCE_NOT_FOUND");
    }

    public String getErrorCode() {
        return errorCode;
    }
}
