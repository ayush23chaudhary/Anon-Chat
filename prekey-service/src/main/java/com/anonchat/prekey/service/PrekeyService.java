package com.anonchat.prekey.service;

import com.anonchat.common.exception.ResourceNotFoundException;
import com.anonchat.common.exception.ValidationException;
import com.anonchat.prekey.dto.PrekeyResponseDto;
import com.anonchat.prekey.dto.UploadOneTimePrekeyRequestDto;
import com.anonchat.prekey.dto.UploadPrekeyRequestDto;
import com.anonchat.prekey.dto.UserBundleResponseDto;
import com.anonchat.prekey.entity.OneTimePrekey;
import com.anonchat.prekey.entity.Prekey;
import com.anonchat.prekey.repository.OneTimePrekeyRepository;
import com.anonchat.prekey.repository.PrekeyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

/**
 * Prekey service for managing Signal-style key distribution.
 * Server acts as prekey store only - never has access to private keys.
 */
@Slf4j
@Service
public class PrekeyService {

    private final PrekeyRepository prekeyRepository;
    private final OneTimePrekeyRepository oneTimePrekeyRepository;

    // In production, this would be fetched from a secure key store
    private static final String IDENTITY_KEY_PLACEHOLDER = "IDENTITY_KEY_PLACEHOLDER";

    public PrekeyService(
            PrekeyRepository prekeyRepository,
            OneTimePrekeyRepository oneTimePrekeyRepository
    ) {
        this.prekeyRepository = prekeyRepository;
        this.oneTimePrekeyRepository = oneTimePrekeyRepository;
    }

    /**
     * Upload a prekey for the authenticated user.
     * Server stores the public key only.
     */
    @Transactional
    public void uploadPrekey(String userId, UploadPrekeyRequestDto request) {
        log.info("Prekey upload for user: {}", userId);

        // Check if prekey with this ID already exists
        Optional<Prekey> existingPrekey = prekeyRepository.findByUserIdAndPrekeyId(userId, request.getPrekeyId());
        if (existingPrekey.isPresent()) {
            throw new ValidationException("Prekey with this ID already exists", "PREKEY_EXISTS");
        }

        Prekey prekey = Prekey.builder()
                .userId(userId)
                .prekeyId(request.getPrekeyId())
                .publicKey(request.getPublicKey())
                .signature(request.getSignature())
                .build();

        prekeyRepository.save(prekey);
        log.debug("Prekey stored for user: {}", userId);
    }

    /**
     * Upload one or more one-time prekeys.
     */
    @Transactional
    public void uploadOneTimePrekey(String userId, UploadOneTimePrekeyRequestDto request) {
        log.info("One-time prekey upload for user: {}", userId);

        // Check if OTP key with this ID already exists
        Optional<OneTimePrekey> existingOtp = oneTimePrekeyRepository.findByUserIdAndOtPrekeyId(userId, request.getOtPrekeyId());
        if (existingOtp.isPresent()) {
            throw new ValidationException("One-time prekey with this ID already exists", "OTP_KEY_EXISTS");
        }

        OneTimePrekey otPrekey = OneTimePrekey.builder()
                .userId(userId)
                .otPrekeyId(request.getOtPrekeyId())
                .publicKey(request.getPublicKey())
                .build();

        oneTimePrekeyRepository.save(otPrekey);
        log.debug("One-time prekey stored for user: {}", userId);
    }

    /**
     * Get prekey bundle for a specific user for key exchange.
     * This is used by a client to establish an encrypted session with another user.
     * Security Note: Server returns public keys only.
     */
    public UserBundleResponseDto getUserBundle(String userId) {
        log.debug("Fetching prekey bundle for user: {}", userId);

        // In production, verify user exists first
        // var user = userService.getUserById(userId);

        // Get latest unused prekey
        Prekey prekey = prekeyRepository.findLatestUnusedPrekey(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No prekeys available for user", "NO_PREKEYS_AVAILABLE"));

        // Get an available one-time prekey
        OneTimePrekey otPrekey = oneTimePrekeyRepository.findByUserIdAndIsUsedFalse(userId)
                .stream()
                .findFirst()
                .orElse(null);

        // Mark keys as used
        prekey.setIsUsed(true);
        prekey.setClaimedAt(Instant.now());
        prekeyRepository.save(prekey);

        if (otPrekey != null) {
            otPrekey.setIsUsed(true);
            otPrekey.setClaimedAt(Instant.now());
            oneTimePrekeyRepository.save(otPrekey);
        }

        PrekeyResponseDto prekeyResponse = PrekeyResponseDto.builder()
                .prekeyId(prekey.getPrekeyId())
                .publicKey(prekey.getPublicKey())
                .signature(prekey.getSignature())
                .build();

        PrekeyResponseDto otPrekeyResponse = otPrekey != null ?
                PrekeyResponseDto.builder()
                        .prekeyId(otPrekey.getOtPrekeyId())
                        .publicKey(otPrekey.getPublicKey())
                        .build()
                : null;

        return UserBundleResponseDto.builder()
                .userId(userId)
                .identityKey(IDENTITY_KEY_PLACEHOLDER)
                .signedPrekey(prekeyResponse)
                .oneTimePrekey(otPrekeyResponse)
                .build();
    }

    /**
     * Get count of available prekeys for a user.
     */
    public long getAvailablePrekeyCount(String userId) {
        return prekeyRepository.countByUserIdAndIsUsedFalse(userId);
    }

    /**
     * Get count of available one-time prekeys for a user.
     */
    public long getAvailableOneTimePrekeyCount(String userId) {
        return oneTimePrekeyRepository.countByUserIdAndIsUsedFalse(userId);
    }
}
