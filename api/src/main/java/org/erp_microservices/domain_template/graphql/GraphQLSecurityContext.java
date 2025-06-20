package org.erp_microservices.domain_template.graphql;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

@Component
@Slf4j
public class GraphQLSecurityContext {

    public String getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }

        if (authentication instanceof JwtAuthenticationToken) {
            Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
            return jwt.getSubject();
        }

        return authentication.getName();
    }

    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }

    public boolean hasScope(String scope) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof JwtAuthenticationToken)) {
            return false;
        }

        Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
        String scopes = jwt.getClaimAsString("scope");
        if (scopes == null) {
            return false;
        }

        return Arrays.asList(scopes.split(" ")).contains(scope);
    }

    public Map<String, Object> getJwtClaims() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof JwtAuthenticationToken)) {
            return Collections.emptyMap();
        }

        Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
        return jwt.getClaims();
    }
}