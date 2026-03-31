package com.anonchat.queue.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO for queued message item.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueuedMessageDto {
    private String messageId;
    private String senderId;
    private String recipientId;
    private String encryptedContent;
    private String messageType;
    private Instant createdAt;
    private Integer retryCount;
}
