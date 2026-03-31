package com.anonchat.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

/**
 * User Entity.
 * 
 * Represents a user in the anonymous-first system.
 * No passwords stored - authentication via key verification.
 */
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username", name = "uk_users_username")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 32)
    private String username;

    /**
     * Base64-encoded identity public key.
     * Used for verification and encryption setup.
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String identityPublicKey;

    /**
     * Optional display name for user profile.
     */
    @Column(length = 128)
    private String displayName;

    /**
     * Account status.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    @Version
    @Column(nullable = false)
    private Long version;

    /**
     * Check if user account is active.
     */
    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }
}
