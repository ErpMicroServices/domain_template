package org.erp_microservices.domain_template.graphql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GraphQL Security Context Tests")
class GraphQLSecurityContextTest {

    private GraphQLSecurityContext securityContext;

    @Mock
    private SecurityContext mockSecurityContext;

    @BeforeEach
    void setUp() {
        securityContext = new GraphQLSecurityContext();
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should retrieve current authenticated user from JWT")
    void getCurrentUser_withAuthenticatedUser_shouldReturnUsername() {
        // Given
        Jwt jwt = createMockJwt("user123");
        Authentication authentication = new JwtAuthenticationToken(jwt);
        when(mockSecurityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(mockSecurityContext);

        // When
        String username = securityContext.getCurrentUser();

        // Then
        assertThat(username).isEqualTo("user123");
    }

    @Test
    @DisplayName("Should return null when no authentication present")
    void getCurrentUser_withNoAuthentication_shouldReturnNull() {
        // Given
        when(mockSecurityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(mockSecurityContext);

        // When
        String username = securityContext.getCurrentUser();

        // Then
        assertThat(username).isNull();
    }

    @Test
    @DisplayName("Should check if user is authenticated")
    void isAuthenticated_withAuthenticatedUser_shouldReturnTrue() {
        // Given
        Jwt jwt = createMockJwt("user123");
        Authentication authentication = new JwtAuthenticationToken(jwt);
        authentication.setAuthenticated(true);
        when(mockSecurityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(mockSecurityContext);

        // When
        boolean authenticated = securityContext.isAuthenticated();

        // Then
        assertThat(authenticated).isTrue();
    }

    @Test
    @DisplayName("Should return false when user not authenticated")
    void isAuthenticated_withUnauthenticatedUser_shouldReturnFalse() {
        // Given
        when(mockSecurityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(mockSecurityContext);

        // When
        boolean authenticated = securityContext.isAuthenticated();

        // Then
        assertThat(authenticated).isFalse();
    }

    @Test
    @DisplayName("Should check if user has specific scope")
    void hasScope_withMatchingScope_shouldReturnTrue() {
        // Given
        Jwt jwt = createMockJwtWithScopes("read", "write");
        Authentication authentication = new JwtAuthenticationToken(jwt);
        when(mockSecurityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(mockSecurityContext);

        // When
        boolean hasScope = securityContext.hasScope("read");

        // Then
        assertThat(hasScope).isTrue();
    }

    @Test
    @DisplayName("Should return false when user lacks specific scope")
    void hasScope_withoutMatchingScope_shouldReturnFalse() {
        // Given
        Jwt jwt = createMockJwtWithScopes("read", "write");
        Authentication authentication = new JwtAuthenticationToken(jwt);
        when(mockSecurityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(mockSecurityContext);

        // When
        boolean hasScope = securityContext.hasScope("admin");

        // Then
        assertThat(hasScope).isFalse();
    }

    @Test
    @DisplayName("Should extract JWT claims")
    void getJwtClaims_withValidJwt_shouldReturnClaims() {
        // Given
        Jwt jwt = createMockJwt("user123");
        Authentication authentication = new JwtAuthenticationToken(jwt);
        when(mockSecurityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(mockSecurityContext);

        // When
        Map<String, Object> claims = securityContext.getJwtClaims();

        // Then
        assertThat(claims).isNotNull();
        assertThat(claims).containsKey("sub");
        assertThat(claims.get("sub")).isEqualTo("user123");
    }

    @Test
    @DisplayName("Should return empty claims when no JWT present")
    void getJwtClaims_withNoJwt_shouldReturnEmptyMap() {
        // Given
        when(mockSecurityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(mockSecurityContext);

        // When
        Map<String, Object> claims = securityContext.getJwtClaims();

        // Then
        assertThat(claims).isEmpty();
    }

    private Jwt createMockJwt(String subject) {
        return Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .claim("sub", subject)
                .claim("scope", "read write")
                .issuedAt(Instant.now().minusSeconds(60))
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }

    private Jwt createMockJwtWithScopes(String... scopes) {
        return Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .claim("sub", "user123")
                .claim("scope", String.join(" ", scopes))
                .issuedAt(Instant.now().minusSeconds(60))
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }
}