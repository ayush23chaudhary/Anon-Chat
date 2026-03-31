package com.anonchat.prekey.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for prekey response.
 * Contains public key information for establishing encrypted sessions.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrekeyResponseDto {
    private Integer prekeyId;
    private String publicKey;
    private String signature;
}
