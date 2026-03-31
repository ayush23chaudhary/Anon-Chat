package com.anonchat.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO for message response.
 * Never decrypts content - returns encrypted data as-is.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponseDto {
    private String id;
    private String senderId;
    private String recipientId;
    private String encryptedContent;
    private String messageType;
    private Boolean isDelivered;
    private Instant deliveredAt;
    private Instant createdAt;
}
