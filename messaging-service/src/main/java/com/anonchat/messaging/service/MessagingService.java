package com.anonchat.messaging.service;

import com.anonchat.messaging.dto.MessageResponseDto;
import com.anonchat.messaging.dto.SendMessageRequestDto;
import com.anonchat.messaging.entity.Message;
import com.anonchat.messaging.repository.MessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Messaging service for message operations.
 * Server acts as relay only - NEVER decrypts messages.
 */
@Slf4j
@Service
public class MessagingService {

    private final MessageRepository messageRepository;

    public MessagingService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    /**
     * Store an encrypted message.
     * Message is pre-encrypted by client - server never decrypts it.
     */
    @Transactional
    public MessageResponseDto storeMessage(String senderId, SendMessageRequestDto request) {
        log.info("Storing encrypted message from {} to {}", senderId, request.getRecipientId());

        Message message = Message.builder()
                .senderId(senderId)
                .recipientId(request.getRecipientId())
                .encryptedContent(request.getEncryptedContent())
                .messageType(request.getMessageType())
                .build();

        Message savedMessage = messageRepository.save(message);
        log.debug("Message stored with ID: {}", savedMessage.getId());

        return toResponseDto(savedMessage);
    }

    /**
     * Get pending messages for a user.
     * These are messages that haven't been delivered yet.
     */
    public List<MessageResponseDto> getPendingMessages(String userId) {
        log.debug("Fetching pending messages for user: {}", userId);
        
        List<Message> pendingMessages = messageRepository.findByRecipientIdAndIsDeliveredFalse(userId);
        return pendingMessages.stream()
                .map(this::toResponseDto)
                .toList();
    }

    /**
     * Mark message as delivered.
     * Called by recipient after receiving and decrypting.
     */
    @Transactional
    public void markAsDelivered(String messageId, String recipientId) {
        log.debug("Marking message {} as delivered for user {}", messageId, recipientId);
        
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new com.anonchat.common.exception.ResourceNotFoundException(
                        "Message not found", "MESSAGE_NOT_FOUND"));

        if (!message.getRecipientId().equals(recipientId)) {
            throw new IllegalArgumentException("User cannot mark other user's messages as delivered");
        }

        message.setIsDelivered(true);
        message.setDeliveredAt(Instant.now());
        messageRepository.save(message);
        
        log.debug("Message marked as delivered");
    }

    /**
     * Get count of pending messages for a user.
     */
    public long getPendingMessageCount(String userId) {
        return messageRepository.countByRecipientIdAndIsDeliveredFalse(userId);
    }

    /**
     * Convert Message entity to response DTO.
     */
    private MessageResponseDto toResponseDto(Message message) {
        return MessageResponseDto.builder()
                .id(message.getId())
                .senderId(message.getSenderId())
                .recipientId(message.getRecipientId())
                .encryptedContent(message.getEncryptedContent())
                .messageType(message.getMessageType())
                .isDelivered(message.getIsDelivered())
                .deliveredAt(message.getDeliveredAt())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
