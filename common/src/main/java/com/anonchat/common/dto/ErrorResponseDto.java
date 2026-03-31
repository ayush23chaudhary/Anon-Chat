package com.anonchat.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for API error responses.
 * Never includes sensitive information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDto {
    private String code;
    private String message;
    private Long timestamp;
    private String path;

    public static ErrorResponseDto of(String code, String message, String path) {
        return ErrorResponseDto.builder()
                .code(code)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .path(path)
                .build();
    }
}
