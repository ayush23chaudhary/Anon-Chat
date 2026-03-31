package com.anonchat.prekey.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for uploading one-time prekeys.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadOneTimePrekeyRequestDto {

    @NotNull(message = "One-time prekey ID cannot be null")
    private Integer otPrekeyId;

    @NotBlank(message = "Public key cannot be blank")
    private String publicKey;
}
