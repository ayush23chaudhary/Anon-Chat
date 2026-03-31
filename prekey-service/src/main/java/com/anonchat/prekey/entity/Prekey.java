package com.anonchat.prekey.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Prekey entity for Signal-style key exchange.
 * Server stores and distributes these public keys (not keying material).
 * Security Note: Server NEVER has access to private keys.
 */
@Entity
@Table(name = "prekeys", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_is_used", columnList = "is_used"),
        @Index(name = "idx_created_at", columnList = "created_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Prekey {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private String userId;

    /**
     * The prekey ID (sequence number assigned by client).
     * Used in conjunction with public key to identify this prekey.
     */
    @Column(name = "prekey_id", nullable = false, updatable = false)
    private Integer prekeyId;

    /**
     * Public key (base64 encoded).
     * This is the encrypted public key that clients have generated locally.
     */
    @Column(name = "public_key", nullable = false, length = 1024)
    private String publicKey;

    /**
     * Optional prekey signature (base64 encoded).
     * Used for key verification.
     */
    @Column(name = "signature", length = 256)
    private String signature;

    /**
     * Whether this prekey has been claimed/used.
     */
    @Column(name = "is_used", nullable = false)
    private Boolean isUsed;

    /**
     * When this prekey was claimed (if at all).
     */
    @Column(name = "claimed_at")
    private Instant claimedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Version
    @Column(name = "version")
    private Long version;

    @PrePersist
    protected void onCreate() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = Instant.now();
        this.isUsed = false;
    }
}
