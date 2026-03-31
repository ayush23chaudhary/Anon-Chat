package com.anonchat.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for WebSocket incoming messages.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessageDto {
    private String type; // "MESSAGE", "DELIVERY_ACK", "STATUS", etc.
    private String recipientId;
    private String encryptedContent;
    private String messageType;
    private String messageId; // For acknowledgments
}
