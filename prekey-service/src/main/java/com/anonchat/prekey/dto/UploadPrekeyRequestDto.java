package com.anonchat.prekey.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for uploading prekeys.
 * Clients generate keys locally and submit public keys to server.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadPrekeyRequestDto {

    @NotNull(message = "Prekey ID cannot be null")
    private Integer prekeyId;

    @NotBlank(message = "Public key cannot be blank")
    private String publicKey;

    private String signature;
}
