package com.anonchat.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for login request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 32, message = "Username must be between 3 and 32 characters")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 12, max = 128, message = "Password must be between 12 and 128 characters")
    private String password;
}
