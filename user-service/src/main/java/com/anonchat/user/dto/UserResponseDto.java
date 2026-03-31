package com.anonchat.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO for user response.
 * Never includes password or sensitive data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private String id;
    private String username;
    private String email;
    private String displayName;
    private Boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;
}
