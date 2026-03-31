package com.anonchat.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AuthResponse DTO.
 * 
 * Returns authentication result with JWT token and user info.
 * Minimal user data for anonymous-first system.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    /**
     * JWT access token for authentication.
     */
    private String accessToken;

    /**
     * User's unique identifier (UUID).
     */
    private String userId;

    /**
     * Username.
     */
    private String username;

    /**
     * Token type (always "Bearer").
     */
    @Builder.Default
    private String tokenType = "Bearer";

    /**
     * Token expiry time in seconds.
     */
    private Long expiresIn;
}
