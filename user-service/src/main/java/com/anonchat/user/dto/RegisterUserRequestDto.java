package com.anonchat.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user registration request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserRequestDto {

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 32, message = "Username must be between 3 and 32 characters")
    private String username;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 12, max = 128, message = "Password must be between 12 and 128 characters")
    private String password;

    @Size(max = 255, message = "Display name must not exceed 255 characters")
    private String displayName;
}
