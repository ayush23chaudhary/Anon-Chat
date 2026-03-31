package com.anonchat.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * AnonChat API Gateway entry point.
 * This is the main application serving as the central entry point.
 */
@SpringBootApplication(scanBasePackages = {
    "com.anonchat.gateway",
    "com.anonchat.auth",
    "com.anonchat.user",
    "com.anonchat.common"
})
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
