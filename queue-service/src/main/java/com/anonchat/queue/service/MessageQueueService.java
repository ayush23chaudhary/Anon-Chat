package com.anonchat.queue.service;

import com.anonchat.queue.dto.QueuedMessageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Queue service using Redis for high-performance offline message queuing.
 * Messages are encrypted end-to-end, server never decrypts.
 * Queue entries are automatically expired after TTL.
 */
@Slf4j
@Service
public class MessageQueueService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private static final long QUEUE_TTL_DAYS = 7; // Retry undelivered messages for 7 days

    public MessageQueueService(RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Add message to queue for a user.
     */
    public void queueMessage(QueuedMessageDto message) {
        String queueKey = "queue:" + message.getRecipientId();
        try {
            String serialized = objectMapper.writeValueAsString(message);
            redisTemplate.opsForList().rightPush(queueKey, serialized);
            
            // Set expiry on the list if it doesn't have one
            redisTemplate.expire(queueKey, QUEUE_TTL_DAYS, TimeUnit.DAYS);
            
            log.debug("Message queued for user: {}", message.getRecipientId());
        } catch (Exception e) {
            log.error("Failed to queue message: {}", e.getMessage());
            throw new RuntimeException("Failed to queue message", e);
        }
    }

    /**
     * Get all queued messages for a user.
     */
    public List<QueuedMessageDto> getQueuedMessages(String userId) {
        String queueKey = "queue:" + userId;
        List<String> messages = redisTemplate.opsForList().range(queueKey, 0, -1);
        
        List<QueuedMessageDto> result = new ArrayList<>();
        if (messages != null) {
            for (String message : messages) {
                try {
                    QueuedMessageDto msg = objectMapper.readValue(message, QueuedMessageDto.class);
                    result.add(msg);
                } catch (Exception e) {
                    log.error("Failed to deserialize queued message: {}", e.getMessage());
                }
            }
        }
        
        log.debug("Retrieved {} queued messages for user: {}", result.size(), userId);
        return result;
    }

    /**
     * Remove a message from queue after delivery.
     */
    public void removeQueuedMessage(String userId, String messageId) {
        String queueKey = "queue:" + userId;
        List<String> messages = redisTemplate.opsForList().range(queueKey, 0, -1);
        
        if (messages != null) {
            for (String message : messages) {
                try {
                    QueuedMessageDto msg = objectMapper.readValue(message, QueuedMessageDto.class);
                    if (msg.getMessageId().equals(messageId)) {
                        redisTemplate.opsForList().remove(queueKey, 1, message);
                        log.debug("Removed message {} from queue for user: {}", messageId, userId);
                        break;
                    }
                } catch (Exception e) {
                    log.error("Failed to process queued message: {}", e.getMessage());
                }
            }
        }
    }

    /**
     * Get count of queued messages for a user.
     */
    public long getQueuedMessageCount(String userId) {
        String queueKey = "queue:" + userId;
        Long count = redisTemplate.opsForList().size(queueKey);
        return count != null ? count : 0L;
    }

    /**
     * Clear all queued messages for a user.
     * Use with caution - should only be done when explicitly requested by user.
     */
    public void clearQueue(String userId) {
        String queueKey = "queue:" + userId;
        redisTemplate.delete(queueKey);
        log.info("Cleared queue for user: {}", userId);
    }
}
