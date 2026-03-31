package com.anonchat.queue.service;

import org.springframework.stereotype.Service;

/**
 * Queue Service.
 * 
 * Manages Redis-based message queue for offline recipients.
 * Security: Messages remain encrypted in queue; server never decrypts.
 */
@Service
public class QueueService {
    // Queue business logic will be implemented here
}
