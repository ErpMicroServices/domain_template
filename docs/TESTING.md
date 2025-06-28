# Testing Guide

This guide explains how to run tests with OAuth2 authentication.

## OAuth2 Mock Server Setup

We use `navikt/mock-oauth2-server` for testing OAuth2 functionality. This provides a lightweight mock server that simulates OAuth2 authentication without requiring external dependencies.

### Local Testing with Docker Compose

To run tests locally with the mock OAuth2 server:

```bash
# Start the test environment
docker-compose -f docker-compose.test.yml up -d

# Run tests
./gradlew test

# Stop the test environment
docker-compose -f docker-compose.test.yml down
```

The mock OAuth2 server runs on port 8180 and provides:
- Token endpoint: `http://localhost:8180/default/token`
- JWKS endpoint: `http://localhost:8180/default/jwks`
- Discovery endpoint: `http://localhost:8180/default/.well-known/openid-configuration`

### CI/CD Integration

The GitHub Actions workflow automatically starts the mock OAuth2 server as a service. No additional configuration is needed.

### Test Configuration

The test profile (`application-test.yml`) is configured to use the mock OAuth2 server:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${OAUTH2_ISSUER_URI:http://localhost:8180/default}
          jwk-set-uri: ${OAUTH2_JWK_SET_URI:http://localhost:8180/default/jwks}
```

### Obtaining Test Tokens

The `OAuth2SecuritySteps` class includes a method to obtain valid test tokens:

```java
private String obtainValidAccessToken() {
    // Requests a token from the mock server
    // Uses client credentials grant with test-client/test-secret
}
```

### Mock Server Configuration

The mock server is configured via `docker/mock-oauth2/config.json` to:
- Accept any client credentials
- Issue tokens with configurable claims
- Support standard OAuth2 flows

## Running Different Test Types

```bash
# Unit tests only
./gradlew test -x integrationTest

# Integration tests only
./gradlew integrationTest

# BDD tests only
./gradlew bddTest

# All tests
./gradlew check
```

## Troubleshooting

1. **Port conflicts**: If port 8180 is in use, modify `docker-compose.test.yml`
2. **Token validation failures**: Check that the mock server is running and accessible
3. **CI/CD failures**: Ensure the OAuth2 service is healthy before tests run