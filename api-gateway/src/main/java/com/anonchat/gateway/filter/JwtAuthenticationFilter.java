package com.anonchat.gateway.filter;

import com.anonchat.common.constant.SecurityConstants;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * JWT authentication filter.
 * Validates JWT tokens and sets authentication context.
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final SecretKey secretKey;

    public JwtAuthenticationFilter(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    protected void doFilterInternal(jakarta.servlet.http.HttpServletRequest request,
                                   jakarta.servlet.http.HttpServletResponse response,
                                   jakarta.servlet.FilterChain filterChain) throws jakarta.servlet.ServletException, java.io.IOException {
        try {
            String authHeader = request.getHeader(SecurityConstants.AUTHORIZATION_HEADER);
            
            if (authHeader != null && authHeader.startsWith(SecurityConstants.BEARER_PREFIX)) {
                String token = authHeader.substring(SecurityConstants.BEARER_PREFIX.length());
                
                try {
                    var claims = Jwts.parser()
                            .verifyWith(secretKey)
                            .build()
                            .parseSignedClaims(token)
                            .getPayload();

                    String userId = claims.getSubject();
                    String username = (String) claims.get(SecurityConstants.JWT_CLAIM_USERNAME);

                    // Set authentication context
                    var authentication = new UsernamePasswordAuthenticationToken(
                            userId, null, java.util.Collections.emptyList());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    // Add userId to request for downstream services
                    request.setAttribute("userId", userId);
                    request.setAttribute("username", username);

                    log.debug("JWT token validated for user: {}", username);
                } catch (JwtException e) {
                    log.debug("Invalid JWT token: {}", e.getMessage());
                    response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            }
        } catch (Exception e) {
            log.error("Error processing authentication filter: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
