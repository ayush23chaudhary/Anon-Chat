package com.anonchat.messaging.controller;

import com.anonchat.messaging.dto.MessageResponseDto;
import com.anonchat.messaging.dto.SendMessageRequestDto;
import com.anonchat.messaging.service.MessagingService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Messaging REST controller for message operations.
 * All endpoints require authentication.
 * WebSocket provides real-time messaging for online users.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/messages")
public class MessagingController {

    private final MessagingService messagingService;

    public MessagingController(MessagingService messagingService) {
        this.messagingService = messagingService;
    }

    /**
     * POST /api/v1/messages/send
     * Send an encrypted message.
     * User ID is extracted from JWT token.
     */
    @PostMapping("/send")
    public ResponseEntity<MessageResponseDto> sendMessage(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody SendMessageRequestDto request) {
        log.info("Message send request from user: {}", userId);
        MessageResponseDto response = messagingService.storeMessage(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/v1/messages/pending
     * Get all pending messages for authenticated user.
     * These messages have not been delivered yet.
     */
    @GetMapping("/pending")
    public ResponseEntity<List<MessageResponseDto>> getPendingMessages(
            @RequestHeader("X-User-Id") String userId) {
        log.debug("Pending messages request for user: {}", userId);
        List<MessageResponseDto> messages = messagingService.getPendingMessages(userId);
        return ResponseEntity.ok(messages);
    }

    /**
     * GET /api/v1/messages/pending/count
     * Get count of pending messages.
     */
    @GetMapping("/pending/count")
    public ResponseEntity<Map<String, Long>> getPendingMessageCount(
            @RequestHeader("X-User-Id") String userId) {
        log.debug("Pending message count request for user: {}", userId);
        long count = messagingService.getPendingMessageCount(userId);
        return ResponseEntity.ok(Map.of("pending_count", count));
    }

    /**
     * POST /api/v1/messages/{messageId}/acknowledge
     * Acknowledge that a message has been received and decrypted.
     */
    @PostMapping("/{messageId}/acknowledge")
    public ResponseEntity<Void> acknowledgeMessage(
            @PathVariable String messageId,
            @RequestHeader("X-User-Id") String userId) {
        log.debug("Acknowledge request for message: {}", messageId);
        messagingService.markAsDelivered(messageId, userId);
        return ResponseEntity.ok().build();
    }
}

// Helper class for response
@lombok.Data
@lombok.Builder
class PendingCountResponse {
    private Long pendingCount;
}
