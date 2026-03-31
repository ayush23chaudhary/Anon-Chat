package com.anonchat.prekey.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * One-time prekey entity.
 * These are single-use prekeys rotated frequently.
 * After being claimed, they are marked as used and eventually deleted.
 */
@Entity
@Table(name = "one_time_prekeys", indexes = {
        @Index(name = "idx_user_id_otp", columnList = "user_id"),
        @Index(name = "idx_is_used_otp", columnList = "is_used")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OneTimePrekey {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private String userId;

    /**
     * One-time key ID assigned by client.
     */
    @Column(name = "ot_prekey_id", nullable = false, updatable = false)
    private Integer otPrekeyId;

    /**
     * Public key (base64 encoded).
     */
    @Column(name = "public_key", nullable = false, length = 1024)
    private String publicKey;

    /**
     * Whether this one-time key has been claimed.
     */
    @Column(name = "is_used", nullable = false)
    private Boolean isUsed;

    /**
     * When this key was claimed.
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
