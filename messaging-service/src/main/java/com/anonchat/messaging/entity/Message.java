package com.anonchat.messaging.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Message entity for persisted messages.
 * Messages are ALWAYS stored encrypted.
 * Server NEVER decrypts messages - they are encrypted end-to-end by clients.
 */
@Entity
@Table(name = "messages", indexes = {
        @Index(name = "idx_sender_id", columnList = "sender_id"),
        @Index(name = "idx_recipient_id", columnList = "recipient_id"),
        @Index(name = "idx_created_at", columnList = "created_at"),
        @Index(name = "idx_is_delivered", columnList = "is_delivered")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "sender_id", nullable = false, updatable = false)
    private String senderId;

    @Column(name = "recipient_id", nullable = false, updatable = false)
    private String recipientId;

    /**
     * Encrypted message content (base64 encoded).
     * Server NEVER decrypts this - it's encrypted end-to-end by client.
     * Security Note: This is always encrypted data, never plaintext.
     */
    @Column(name = "encrypted_content", nullable = false, columnDefinition = "TEXT")
    private String encryptedContent;

    /**
     * Message type (e.g., "text", "image_metadata", etc.)
     */
    @Column(name = "message_type", nullable = false, length = 32)
    private String messageType;

    /**
     * Whether recipient has received and decrypted the message.
     */
    @Column(name = "is_delivered", nullable = false)
    private Boolean isDelivered;

    /**
     * When the message was delivered.
     */
    @Column(name = "delivered_at")
    private Instant deliveredAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Version
    @Column(name = "version")
    private Long version;

    @PrePersist
    protected void onCreate() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = Instant.now();
        this.isDelivered = false;
    }
}
