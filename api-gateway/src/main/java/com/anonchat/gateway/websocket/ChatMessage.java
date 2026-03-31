package com.anonchat.gateway.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * ChatMessage DTO for WebSocket communication
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private String roomId;
    private String userId;
    private String content;
    private String type; // USER, SYSTEM, NOTIFICATION
    private Date timestamp;
    private Integer memberCount;
    private String replyToText;

    public ChatMessage(String roomId, String userId, String content, String type, Date timestamp) {
        this.roomId = roomId;
        this.userId = userId;
        this.content = content;
        this.type = type;
        this.timestamp = timestamp;
    }
}
