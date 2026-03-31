package com.anonchat.gateway.config;

import com.anonchat.auth.service.AuthService;
import com.anonchat.auth.config.JwtTokenProvider;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Gateway configuration that explicitly wires auth service components.
 */
@Configuration
@EntityScan(basePackages = {"com.anonchat.user.entity", "com.anonchat.common.entity"})
@EnableJpaRepositories(basePackages = {"com.anonchat.user.repository"})
public class GatewayConfig {

    /**
     * Explicitly create AuthService bean.
     */
    @Bean
    public AuthService authService(JwtTokenProvider jwtTokenProvider, 
                                   PasswordEncoder passwordEncoder) {
        return new AuthService(jwtTokenProvider, passwordEncoder, null);
    }
}
