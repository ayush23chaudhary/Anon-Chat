package com.anonchat.messaging.websocket;

import com.anonchat.messaging.dto.MessageResponseDto;
import com.anonchat.messaging.dto.SendMessageRequestDto;
import com.anonchat.messaging.dto.WebSocketMessageDto;
import com.anonchat.messaging.service.MessagingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket handler for real-time messaging.
 * Manages user connections and message relay.
 * Security Note: Messages are relayed encrypted - server never decrypts.
 */
@Slf4j
@Component
public class MessagingWebSocketHandler extends TextWebSocketHandler {

    private final MessagingService messagingService;
    private final ObjectMapper objectMapper;
    private final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    public MessagingWebSocketHandler(MessagingService messagingService, ObjectMapper objectMapper) {
        this.messagingService = messagingService;
        this.objectMapper = objectMapper;
    }

    /**
     * Handle new WebSocket connection.
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = extractUserId(session);
        if (userId != null) {
            userSessions.put(userId, session);
            log.info("User {} connected to WebSocket", userId);
        } else {
            log.warn("Connection established but userId could not be extracted");
            session.close();
        }
    }

    /**
     * Handle incoming WebSocket messages.
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String userId = extractUserId(session);
        if (userId == null) {
            log.warn("Received message from unauthenticated session");
            session.close();
            return;
        }

        try {
            WebSocketMessageDto wsMessage = objectMapper.readValue(message.getPayload(), WebSocketMessageDto.class);
            
            switch (wsMessage.getType()) {
                case "MESSAGE":
                    handleIncomingMessage(userId, wsMessage);
                    break;
                case "DELIVERY_ACK":
                    handleDeliveryAck(userId, wsMessage);
                    break;
                case "STATUS":
                    handleStatusRequest(userId, session);
                    break;
                default:
                    log.warn("Unknown message type: {}", wsMessage.getType());
            }
        } catch (Exception e) {
            log.error("Error processing WebSocket message: {}", e.getMessage());
        }
    }

    /**
     * Handle connection close.
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        String userId = extractUserId(session);
        if (userId != null) {
            userSessions.remove(userId);
            log.info("User {} disconnected from WebSocket", userId);
        }
    }

    /**
     * Handle incoming encrypted message.
     */
    private void handleIncomingMessage(String senderId, WebSocketMessageDto wsMessage) {
        SendMessageRequestDto request = SendMessageRequestDto.builder()
                .recipientId(wsMessage.getRecipientId())
                .encryptedContent(wsMessage.getEncryptedContent())
                .messageType(wsMessage.getMessageType())
                .build();

        MessageResponseDto stored = messagingService.storeMessage(senderId, request);

        // Try to deliver to recipient if online
        if (userSessions.containsKey(wsMessage.getRecipientId())) {
            deliverMessageToUser(wsMessage.getRecipientId(), stored);
        } else {
            log.debug("Recipient {} is offline, message queued", wsMessage.getRecipientId());
        }
    }

    /**
     * Handle delivery acknowledgment.
     */
    private void handleDeliveryAck(String userId, WebSocketMessageDto wsMessage) {
        if (wsMessage.getMessageId() != null) {
            messagingService.markAsDelivered(wsMessage.getMessageId(), userId);
            log.debug("Message {} marked as delivered", wsMessage.getMessageId());
        }
    }

    /**
     * Handle status request.
     * User requests count of pending messages.
     */
    private void handleStatusRequest(String userId, WebSocketSession session) throws IOException {
        long pendingCount = messagingService.getPendingMessageCount(userId);
        
        Map<String, Object> response = Map.of(
                "type", "STATUS",
                "pending_message_count", pendingCount
        );
        
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
    }

    /**
     * Deliver message to online user.
     */
    private void deliverMessageToUser(String userId, MessageResponseDto message) {
        WebSocketSession session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                Map<String, Object> payload = Map.of(
                        "type", "MESSAGE",
                        "data", message
                );
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(payload)));
                log.debug("Message delivered to online user: {}", userId);
            } catch (IOException e) {
                log.error("Error delivering message to user {}: {}", userId, e.getMessage());
            }
        }
    }

    /**
     * Extract userId from WebSocket session.
     * In production, this would validate JWT from the connection URL or headers.
     */
    private String extractUserId(WebSocketSession session) {
        // TODO: Implement JWT validation here
        // Extract from URL query parameter: ws://localhost:8080/ws?token=<jwt>
        String uri = session.getUri().toString();
        if (uri.contains("userId=")) {
            return uri.substring(uri.indexOf("userId=") + 7).split("&")[0];
        }
        return null;
    }
}
