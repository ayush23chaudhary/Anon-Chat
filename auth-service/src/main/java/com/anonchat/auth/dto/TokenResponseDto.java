package com.anonchat.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for token response.
 * Contains tokens for authenticated user.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponseDto {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;

    public static TokenResponseDto of(String accessToken, String refreshToken, long expiresIn) {
        return TokenResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn / 1000) // Convert to seconds
                .build();
    }
}
