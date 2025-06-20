package org.erp_microservices.domain_template.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OAuth2 Token Validation Service Tests")
class OAuth2TokenValidationServiceTest {

    private OAuth2TokenValidationService tokenValidationService;

    @Mock
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() {
        tokenValidationService = new OAuth2TokenValidationService(jwtDecoder);
    }

    @Test
    @DisplayName("Should validate a valid JWT token")
    void validateToken_withValidToken_shouldReturnTrue() {
        // Given
        String token = "valid.jwt.token";
        Jwt jwt = createMockJwt();
        when(jwtDecoder.decode(token)).thenReturn(jwt);

        // When
        boolean result = tokenValidationService.validateToken(token);

        // Then
        assertThat(result).isTrue();
        verify(jwtDecoder).decode(token);
    }

    @Test
    @DisplayName("Should reject an invalid JWT token")
    void validateToken_withInvalidToken_shouldReturnFalse() {
        // Given
        String token = "invalid.jwt.token";
        when(jwtDecoder.decode(token)).thenThrow(new JwtException("Invalid token"));

        // When
        boolean result = tokenValidationService.validateToken(token);

        // Then
        assertThat(result).isFalse();
        verify(jwtDecoder).decode(token);
    }

    @Test
    @DisplayName("Should reject an expired JWT token")
    void validateToken_withExpiredToken_shouldReturnFalse() {
        // Given
        String token = "expired.jwt.token";
        Jwt jwt = createExpiredMockJwt();
        when(jwtDecoder.decode(token)).thenReturn(jwt);

        // When
        boolean result = tokenValidationService.validateToken(token);

        // Then
        assertThat(result).isFalse();
        verify(jwtDecoder).decode(token);
    }

    @Test
    @DisplayName("Should handle null token")
    void validateToken_withNullToken_shouldReturnFalse() {
        // When
        boolean result = tokenValidationService.validateToken(null);

        // Then
        assertThat(result).isFalse();
        verify(jwtDecoder, never()).decode(anyString());
    }

    @Test
    @DisplayName("Should handle empty token")
    void validateToken_withEmptyToken_shouldReturnFalse() {
        // When
        boolean result = tokenValidationService.validateToken("");

        // Then
        assertThat(result).isFalse();
        verify(jwtDecoder, never()).decode(anyString());
    }

    @Test
    @DisplayName("Should extract claims from valid token")
    void extractClaims_withValidToken_shouldReturnClaims() {
        // Given
        String token = "valid.jwt.token";
        Jwt jwt = createMockJwt();
        when(jwtDecoder.decode(token)).thenReturn(jwt);

        // When
        Map<String, Object> claims = tokenValidationService.extractClaims(token);

        // Then
        assertThat(claims).isNotNull();
        assertThat(claims).containsKey("sub");
        assertThat(claims.get("sub")).isEqualTo("user123");
        assertThat(claims).containsKey("scope");
        verify(jwtDecoder).decode(token);
    }

    @Test
    @DisplayName("Should return empty claims for invalid token")
    void extractClaims_withInvalidToken_shouldReturnEmptyClaims() {
        // Given
        String token = "invalid.jwt.token";
        when(jwtDecoder.decode(token)).thenThrow(new JwtException("Invalid token"));

        // When
        Map<String, Object> claims = tokenValidationService.extractClaims(token);

        // Then
        assertThat(claims).isEmpty();
        verify(jwtDecoder).decode(token);
    }

    @Test
    @DisplayName("Should check if token has required scope")
    void hasScope_withRequiredScope_shouldReturnTrue() {
        // Given
        String token = "valid.jwt.token";
        Jwt jwt = createMockJwt();
        when(jwtDecoder.decode(token)).thenReturn(jwt);

        // When
        boolean result = tokenValidationService.hasScope(token, "read");

        // Then
        assertThat(result).isTrue();
        verify(jwtDecoder).decode(token);
    }

    @Test
    @DisplayName("Should return false if token lacks required scope")
    void hasScope_withoutRequiredScope_shouldReturnFalse() {
        // Given
        String token = "valid.jwt.token";
        Jwt jwt = createMockJwt();
        when(jwtDecoder.decode(token)).thenReturn(jwt);

        // When
        boolean result = tokenValidationService.hasScope(token, "admin");

        // Then
        assertThat(result).isFalse();
        verify(jwtDecoder).decode(token);
    }

    private Jwt createMockJwt() {
        return Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .claim("sub", "user123")
                .claim("scope", "read write")
                .issuedAt(Instant.now().minusSeconds(60))
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }

    private Jwt createExpiredMockJwt() {
        return Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .claim("sub", "user123")
                .claim("scope", "read write")
                .issuedAt(Instant.now().minusSeconds(7200))
                .expiresAt(Instant.now().minusSeconds(3600))
                .build();
    }
}