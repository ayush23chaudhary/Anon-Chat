package com.anonchat.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RegisterRequest DTO.
 * 
 * Handles user registration requests in anonymous-first system.
 * Requirements:
 * - Unique username
 * - Identity public key (base64 encoded)
 * - No passwords (authentication via key verification)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 32, message = "Username must be 3-32 characters")
    private String username;

    @NotBlank(message = "Identity public key is required")
    private String identityPublicKey;

    /**
     * Optional display name for user profile.
     */
    @Size(max = 128, message = "Display name must be under 128 characters")
    private String displayName;
}
