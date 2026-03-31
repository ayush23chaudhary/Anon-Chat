package com.anonchat.security.config;

import com.anonchat.security.filter.JwtAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

/**
 * SecurityConfig - Production-ready Spring Security configuration.
 * 
 * Features:
 * - Stateless JWT authentication
 * - CSRF disabled (for API)
 * - CORS enabled
 * - Public auth endpoints
 * - Protected resource endpoints
 * - WebSocket endpoints permitted
 */
@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Configure HTTP security.
     * 
     * - Disables CSRF (API doesn't need it)
     * - Sets stateless session policy (JWT-based)
     * - Permits public auth endpoints
     * - Permits WebSocket endpoints
     * - Requires authentication for all other endpoints
     * - Adds JWT filter
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("Configuring Spring Security");

        http
                // Disable CSRF for REST API
                .csrf(csrf -> csrf.disable())

                // Enable CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Stateless session
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Authorization rules
                .authorizeHttpRequests(authz -> authz
                        // Public endpoints - Auth
                        .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()

                        // Public endpoints - Health check
                        .requestMatchers("/actuator/health").permitAll()

                        // WebSocket endpoints (will be secured by handshake)
                        .requestMatchers("/ws/**").permitAll()

                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                )

                // Add JWT filter before standard auth filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS configuration.
     * 
     * Allows cross-origin requests from trusted origins.
     * In production, restrict to specific domains.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Allow requests from any origin (restrict in production)
        config.setAllowedOrigins(Collections.singletonList("*"));

        // Allow common HTTP methods
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Allow common headers
        config.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "X-Requested-With"
        ));

        // Allow credentials if needed
        config.setAllowCredentials(false);

        // Cache preflight for 1 hour
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    /**
     * Password encoder bean.
     * Uses BCrypt with default strength (10 rounds).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
