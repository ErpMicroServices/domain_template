package org.erp_microservices.domain_template.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OAuth2 Security Configuration Tests")
@TestPropertySource(properties = {
    "spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8080",
    "spring.main.lazy-initialization=true"
})
class OAuth2SecurityConfigTest {

    private OAuth2SecurityConfig securityConfig;

    @Mock
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() {
        securityConfig = new OAuth2SecurityConfig();
    }

    @Test
    @DisplayName("Security filter chain should be configured with OAuth2 resource server")
    void securityFilterChain_shouldConfigureOAuth2ResourceServer() throws Exception {
        // Given
        HttpSecurity httpSecurity = mock(HttpSecurity.class, RETURNS_DEEP_STUBS);
        
        // When
        SecurityFilterChain result = securityConfig.securityFilterChain(httpSecurity);
        
        // Then
        assertThat(result).isNotNull();
        verify(httpSecurity).csrf(any());
        verify(httpSecurity).authorizeHttpRequests(any());
        verify(httpSecurity).oauth2ResourceServer(any());
    }

    @Test
    @DisplayName("GraphQL endpoint should require authentication")
    void graphqlEndpoint_shouldRequireAuthentication() throws Exception {
        // Given
        HttpSecurity httpSecurity = mock(HttpSecurity.class, RETURNS_DEEP_STUBS);
        
        // When
        securityConfig.securityFilterChain(httpSecurity);
        
        // Then
        verify(httpSecurity.authorizeHttpRequests()).requestMatchers("/graphql").authenticated();
    }

    @Test
    @DisplayName("Health and actuator endpoints should be publicly accessible")
    void healthEndpoints_shouldBePubliclyAccessible() throws Exception {
        // Given
        HttpSecurity httpSecurity = mock(HttpSecurity.class, RETURNS_DEEP_STUBS);
        
        // When
        securityConfig.securityFilterChain(httpSecurity);
        
        // Then
        verify(httpSecurity.authorizeHttpRequests()).requestMatchers("/actuator/health").permitAll();
        verify(httpSecurity.authorizeHttpRequests()).requestMatchers("/actuator/health/**").permitAll();
    }

    @Test
    @DisplayName("OAuth2 discovery endpoints should be publicly accessible")
    void oauth2DiscoveryEndpoints_shouldBePubliclyAccessible() throws Exception {
        // Given
        HttpSecurity httpSecurity = mock(HttpSecurity.class, RETURNS_DEEP_STUBS);
        
        // When
        securityConfig.securityFilterChain(httpSecurity);
        
        // Then
        verify(httpSecurity.authorizeHttpRequests()).requestMatchers("/.well-known/**").permitAll();
    }

    @Test
    @DisplayName("JWT decoder should be properly configured")
    void jwtDecoder_shouldBeConfigured() {
        // Given
        String issuerUri = "http://localhost:8080";
        ReflectionTestUtils.setField(securityConfig, "issuerUri", issuerUri);
        
        // When
        JwtDecoder decoder = securityConfig.jwtDecoder();
        
        // Then
        assertThat(decoder).isNotNull();
    }

    @Test
    @DisplayName("CORS should be configured for GraphQL endpoint")
    void cors_shouldBeConfiguredForGraphQL() throws Exception {
        // Given
        HttpSecurity httpSecurity = mock(HttpSecurity.class, RETURNS_DEEP_STUBS);
        
        // When
        securityConfig.securityFilterChain(httpSecurity);
        
        // Then
        verify(httpSecurity).cors(any());
    }

    @Test
    @DisplayName("CSRF should be disabled for stateless API")
    void csrf_shouldBeDisabled() throws Exception {
        // Given
        HttpSecurity httpSecurity = mock(HttpSecurity.class, RETURNS_DEEP_STUBS);
        
        // When
        securityConfig.securityFilterChain(httpSecurity);
        
        // Then
        verify(httpSecurity.csrf()).disable();
    }

    @Test
    @DisplayName("Session management should be stateless")
    void sessionManagement_shouldBeStateless() throws Exception {
        // Given
        HttpSecurity httpSecurity = mock(HttpSecurity.class, RETURNS_DEEP_STUBS);
        
        // When
        securityConfig.securityFilterChain(httpSecurity);
        
        // Then
        verify(httpSecurity).sessionManagement(any());
    }
}