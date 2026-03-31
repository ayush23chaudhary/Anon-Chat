package com.anonchat.prekey.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user bundle response.
 * Contains identity key and prekeys for establishing encrypted session with a user.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBundleResponseDto {
    private String userId;
    private String identityKey;
    private PrekeyResponseDto signedPrekey;
    private PrekeyResponseDto oneTimePrekey;
}
