package com.anonchat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * AnonChat Main Application Entry Point.
 * 
 * A production-grade secure messaging platform with end-to-end encryption.
 * Server acts as relay and prekey store only - never decrypts messages.
 */
@SpringBootApplication
@ComponentScan(basePackages = {
        "com.anonchat.auth",
        "com.anonchat.user",
        "com.anonchat.keys",
        "com.anonchat.messaging",
        "com.anonchat.websocket",
        "com.anonchat.queue",
        "com.anonchat.security"
})
public class AnonChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnonChatApplication.class, args);
    }
}
