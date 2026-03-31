package com.anonchat.messaging.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket configuration.
 * Sets up WebSocket endpoints and handlers.
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final MessagingWebSocketHandler messagingWebSocketHandler;

    public WebSocketConfig(MessagingWebSocketHandler messagingWebSocketHandler) {
        this.messagingWebSocketHandler = messagingWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(messagingWebSocketHandler, "/ws/messages")
                .setAllowedOrigins("*"); // Configure CORS appropriately for production
    }
}
