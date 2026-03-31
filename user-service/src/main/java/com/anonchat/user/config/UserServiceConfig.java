package com.anonchat.user.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for User Service entity scanning.
 */
@Configuration
@EntityScan(basePackages = {"com.anonchat.user.entity"})
public class UserServiceConfig {
}
