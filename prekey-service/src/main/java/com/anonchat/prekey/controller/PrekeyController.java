package com.anonchat.prekey.controller;

import com.anonchat.prekey.dto.*;
import com.anonchat.prekey.service.PrekeyService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Prekey controller for key management.
 * All endpoints require authentication.
 * Clients submit their public keys; server distributes them for key exchange.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/prekeys")
public class PrekeyController {

    private final PrekeyService prekeyService;

    public PrekeyController(PrekeyService prekeyService) {
        this.prekeyService = prekeyService;
    }

    /**
     * POST /api/v1/prekeys/upload
     * Upload a prekey.
     * User is extracted from JWT token in Authorization header.
     */
    @PostMapping("/upload")
    public ResponseEntity<Void> uploadPrekey(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody UploadPrekeyRequestDto request) {
        log.info("Prekey upload request from user: {}", userId);
        prekeyService.uploadPrekey(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * POST /api/v1/prekeys/one-time/upload
     * Upload a one-time prekey.
     */
    @PostMapping("/one-time/upload")
    public ResponseEntity<Void> uploadOneTimePrekey(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody UploadOneTimePrekeyRequestDto request) {
        log.info("One-time prekey upload request from user: {}", userId);
        prekeyService.uploadOneTimePrekey(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * GET /api/v1/prekeys/{userId}
     * Get prekey bundle for a user.
     * Used to establish encrypted session with the target user.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserBundleResponseDto> getUserBundle(@PathVariable String userId) {
        log.debug("Bundle request for user: {}", userId);
        UserBundleResponseDto bundle = prekeyService.getUserBundle(userId);
        return ResponseEntity.ok(bundle);
    }

    /**
     * GET /api/v1/prekeys/{userId}/status
     * Get prekey availability status for a user.
     */
    @GetMapping("/{userId}/status")
    public ResponseEntity<PrekeyStatusDto> getPrekeyStatus(@PathVariable String userId) {
        log.debug("Prekey status request for user: {}", userId);
        long availablePrekeys = prekeyService.getAvailablePrekeyCount(userId);
        long availableOtpKeys = prekeyService.getAvailableOneTimePrekeyCount(userId);
        
        PrekeyStatusDto status = PrekeyStatusDto.builder()
                .userId(userId)
                .availablePrekeyCount(availablePrekeys)
                .availableOneTimePrekeyCount(availableOtpKeys)
                .build();
        
        return ResponseEntity.ok(status);
    }
}
