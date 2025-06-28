package org.erp_microservices.domain_template.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2TokenValidationService {

    private final JwtDecoder jwtDecoder;

    public boolean validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        try {
            Jwt jwt = jwtDecoder.decode(token);
            return !isTokenExpired(jwt);
        } catch (JwtException e) {
            log.debug("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public Map<String, Object> extractClaims(String token) {
        if (token == null || token.isEmpty()) {
            return Collections.emptyMap();
        }

        try {
            Jwt jwt = jwtDecoder.decode(token);
            return jwt.getClaims();
        } catch (JwtException e) {
            log.debug("Failed to extract claims: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }

    public boolean hasScope(String token, String requiredScope) {
        if (token == null || token.isEmpty() || requiredScope == null) {
            return false;
        }

        try {
            Jwt jwt = jwtDecoder.decode(token);
            String scopes = jwt.getClaimAsString("scope");
            if (scopes == null) {
                return false;
            }
            return Arrays.asList(scopes.split(" ")).contains(requiredScope);
        } catch (JwtException e) {
            log.debug("Failed to check scope: {}", e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(Jwt jwt) {
        Instant expiresAt = jwt.getExpiresAt();
        return expiresAt != null && expiresAt.isBefore(Instant.now());
    }
}