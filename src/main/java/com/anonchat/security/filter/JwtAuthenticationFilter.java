package com.anonchat.security.filter;

import com.anonchat.auth.jwt.JwtService;
import com.anonchat.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

/**
 * JwtAuthenticationFilter - Extract and validate JWT tokens from requests.
 * 
 * Flow:
 * 1. Extract JWT from Authorization header (Bearer token)
 * 2. Validate token signature and expiration
 * 3. Extract user ID and username from token claims
 * 4. Set SecurityContext with authentication
 * 5. Continue filter chain
 * 
 * If token is missing, invalid, or expired:
 * - Log at debug level
 * - Clear SecurityContext
 * - Continue (let endpoint-level security handle it)
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            // Extract JWT from request
            String jwt = extractJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && jwtService.validateToken(jwt)) {
                // Token is valid, extract claims
                UUID userId = jwtService.extractUserId(jwt);
                String username = jwtService.extractUsername(jwt);

                log.debug("JWT validated for user: {}", username);

                // Verify user still exists and is active
                userRepository.findById(userId).ifPresentOrElse(
                        user -> {
                            if (user.isActive()) {
                                // Create authentication token
                                UsernamePasswordAuthenticationToken authentication =
                                        new UsernamePasswordAuthenticationToken(
                                                userId,
                                                null,
                                                Collections.emptyList()
                                        );

                                // Set principal (userId) and credentials
                                authentication.setDetails(username);

                                // Set in security context
                                SecurityContextHolder.getContext().setAuthentication(authentication);

                                log.debug("Authentication set for user: {} ({})", username, userId);
                            } else {
                                log.debug("User is inactive: {}", username);
                                SecurityContextHolder.clearContext();
                            }
                        },
                        () -> {
                            log.debug("User not found: {}", userId);
                            SecurityContextHolder.clearContext();
                        }
                );
            } else {
                log.debug("JWT not found or invalid in request");
                SecurityContextHolder.clearContext();
            }
        } catch (Exception ex) {
            log.debug("Failed to set user authentication in security context", ex);
            SecurityContextHolder.clearContext();
        }

        // Continue filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from Authorization header.
     * Expected format: Authorization: Bearer <jwt_token>
     * 
     * @param request HTTP request
     * @return JWT token or null if not present
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(authHeader) && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length());
        }

        return null;
    }
}
