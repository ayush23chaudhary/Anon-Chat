package com.anonchat.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Objects for User module.
 * Never includes password fields in responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    // User DTOs will be defined here
}
