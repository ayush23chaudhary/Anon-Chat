package com.anonchat.common.constant;

/**
 * Security-related constants.
 * Never hardcode secrets in this class.
 */
public class SecurityConstants {
    public static final String JWT_CLAIM_USER_ID = "userId";
    public static final String JWT_CLAIM_USERNAME = "username";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    
    // Token timing
    public static final long ACCESS_TOKEN_EXPIRY_MS = 15 * 60 * 1000; // 15 minutes
    public static final long REFRESH_TOKEN_EXPIRY_MS = 7 * 24 * 60 * 60 * 1000; // 7 days
    
    // Validation constraints
    public static final int USERNAME_MIN_LENGTH = 3;
    public static final int USERNAME_MAX_LENGTH = 32;
    public static final int PASSWORD_MIN_LENGTH = 12;
    public static final int PASSWORD_MAX_LENGTH = 128;
    
    private SecurityConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }
}
