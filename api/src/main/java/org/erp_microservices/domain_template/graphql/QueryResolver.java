package org.erp_microservices.domain_template.graphql;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class QueryResolver {

    private final GraphQLSecurityContext securityContext;

    @QueryMapping
    public String healthCheck() {
        return "API is healthy and secured with OAuth2";
    }

    @QueryMapping
    public User currentUser() {
        if (!securityContext.isAuthenticated()) {
            return null;
        }

        return User.builder()
                .username(securityContext.getCurrentUser())
                .build();
    }

    @lombok.Data
    @lombok.Builder
    public static class User {
        private String username;
        private String email;
        private java.util.List<String> roles;
    }
}