package com.anonchat.gateway.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket handler for real-time chat in rooms.
 * Manages connections and message broadcasting to room members.
 */
@Slf4j
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private static final Map<String, Map<String, WebSocketSession>> rooms = new ConcurrentHashMap<>();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String roomId = extractRoomId(session.getUri().toString());
        String userId = extractUserId(session.getUri().toString());

        log.info("WebSocket connection established - Room: {}, User: {}", roomId, userId);

        rooms.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>())
             .put(userId, session);

        // Notify room that user joined
        ChatMessage msg = new ChatMessage(
            roomId,
            userId,
            "User joined the room",
            "SYSTEM",
            new Date()
        );
        // Generate unique ID for this message to prevent duplicates
        msg.setId(UUID.randomUUID().toString());
        msg.setMemberCount(rooms.get(roomId).size());
        broadcastToRoom(roomId, msg);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            ChatMessage chatMessage = objectMapper.readValue(message.getPayload(), ChatMessage.class);
            String roomId = chatMessage.getRoomId();

            // Check if this is a typing indicator event
            if ("TYPING".equals(chatMessage.getType())) {
                log.info("Typing event - Room: {}, User: {}, IsTyping: {}", roomId, chatMessage.getUserId(), chatMessage.getIsTyping());
                // Broadcast typing indicator to all OTHER users (not the sender)
                broadcastTypingToRoom(roomId, chatMessage);
            } else {
                log.info("Message received - Room: {}, User: {}, Content: {}", roomId, chatMessage.getUserId(), chatMessage.getContent());
                // Broadcast regular message to all users in the room
                broadcastToRoom(roomId, chatMessage);
            }

        } catch (Exception e) {
            log.error("Error processing message", e);
            session.sendMessage(new TextMessage("{\"error\": \"Invalid message format\"}"));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String roomId = extractRoomId(session.getUri().toString());
        String userId = extractUserId(session.getUri().toString());

        log.info("WebSocket connection closed - Room: {}, User: {}", roomId, userId);

        Map<String, WebSocketSession> room = rooms.get(roomId);
        if (room != null) {
            room.remove(userId);

            if (room.isEmpty()) {
                rooms.remove(roomId);
            } else {
                // Notify room that user left
                ChatMessage msg = new ChatMessage(
                    roomId,
                    userId,
                    "User left the room",
                    "SYSTEM",
                    new Date()
                );
                // Generate unique ID for this message to prevent duplicates
                msg.setId(UUID.randomUUID().toString());
                msg.setMemberCount(room.size());
                broadcastToRoom(roomId, msg);
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket transport error", exception);
        session.close();
    }

    /**
     * Broadcast message to all users in a room
     */
    private void broadcastToRoom(String roomId, ChatMessage message) {
        Map<String, WebSocketSession> room = rooms.get(roomId);
        if (room == null) return;

        String payload = null;
        try {
            payload = objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            log.error("Error serializing message", e);
            return;
        }

        for (WebSocketSession session : room.values()) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(payload));
                }
            } catch (IOException e) {
                log.error("Error sending message to session", e);
            }
        }
    }

    /**
     * Broadcast typing indicator to all OTHER users in the room (exclude sender)
     */
    private void broadcastTypingToRoom(String roomId, ChatMessage message) {
        Map<String, WebSocketSession> room = rooms.get(roomId);
        if (room == null) return;

        String payload = null;
        try {
            payload = objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            log.error("Error serializing typing message", e);
            return;
        }

        String senderId = message.getUserId();
        for (Map.Entry<String, WebSocketSession> entry : room.entrySet()) {
            // Only send typing indicator to other users, not the sender
            if (!entry.getKey().equals(senderId)) {
                try {
                    WebSocketSession session = entry.getValue();
                    if (session.isOpen() && payload != null) {
                        session.sendMessage(new TextMessage(payload));
                    }
                } catch (IOException e) {
                    log.error("Error sending typing indicator to session", e);
                }
            }
        }
    }

    /**
     * Extract room ID from WebSocket URI
     */
    private String extractRoomId(String uri) {
        // URI format: ws://localhost:8081/ws/chat/{roomId}?userId={userId}
        String[] parts = uri.split("/");
        for (int i = 0; i < parts.length - 1; i++) {
            if (parts[i].equals("chat") && i + 1 < parts.length) {
                return parts[i + 1].split("\\?")[0];
            }
        }
        return "default";
    }

    /**
     * Extract user ID from WebSocket URI query parameters
     */
    private String extractUserId(String uri) {
        // URI format: ws://localhost:8081/ws/chat/{roomId}?userId={userId}
        if (uri.contains("userId=")) {
            String userId = uri.split("userId=")[1];
            // Remove any trailing path or fragments
            if (userId.contains("&")) {
                userId = userId.split("&")[0];
            }
            return userId;
        }
        return "anonymous";
    }
}
