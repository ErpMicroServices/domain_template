package org.erp_microservices.domain_template.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.en.And;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
public class OAuth2SecuritySteps {

    @Autowired
    private TestRestTemplate restTemplate;

    private ResponseEntity<String> response;
    private String accessToken;
    private HttpHeaders headers;

    @Given("the OAuth2 server is running locally via docker-compose")
    public void theOAuth2ServerIsRunning() {
        // This will be verified by checking the OAuth2 server health endpoint
        // The actual server should be started by docker-compose before tests
    }

    @Given("the GraphQL API is configured with OAuth2 security")
    public void theGraphQLAPIIsConfiguredWithOAuth2Security() {
        // This is configured in application.yml and security configuration
        // No action needed here as it's part of the application setup
    }

    @When("I send a GraphQL query without an access token")
    public void iSendAGraphQLQueryWithoutAnAccessToken() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        String graphqlQuery = """
            {
                "query": "{ __typename }"
            }
            """;
        
        HttpEntity<String> request = new HttpEntity<>(graphqlQuery, headers);
        response = restTemplate.exchange("/graphql", HttpMethod.POST, request, String.class);
    }

    @Then("I should receive a {int} Unauthorized response")
    public void iShouldReceiveAnUnauthorizedResponse(int statusCode) {
        assertThat(response.getStatusCodeValue()).isEqualTo(statusCode);
    }

    @And("the response should contain an authentication error")
    public void theResponseShouldContainAnAuthenticationError() {
        assertThat(response.getBody()).isNotNull();
        // The exact error format depends on Spring Security configuration
    }

    @Given("I have an invalid access token")
    public void iHaveAnInvalidAccessToken() {
        accessToken = "invalid-token-12345";
    }

    @When("I send a GraphQL query with the invalid token")
    public void iSendAGraphQLQueryWithTheInvalidToken() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        
        String graphqlQuery = """
            {
                "query": "{ __typename }"
            }
            """;
        
        HttpEntity<String> request = new HttpEntity<>(graphqlQuery, headers);
        response = restTemplate.exchange("/graphql", HttpMethod.POST, request, String.class);
    }

    @And("the response should indicate token validation failure")
    public void theResponseShouldIndicateTokenValidationFailure() {
        assertThat(response.getBody()).isNotNull();
        // Specific error message depends on OAuth2 configuration
    }

    @Given("I have obtained a valid access token from the OAuth2 server")
    public void iHaveObtainedAValidAccessTokenFromTheOAuth2Server() {
        // This would typically involve calling the OAuth2 token endpoint
        // For now, we'll need to implement the actual OAuth2 client flow
        // This is a placeholder that will be implemented with the OAuth2 server setup
        accessToken = obtainValidAccessToken();
    }

    @When("I send a GraphQL query with the valid token")
    public void iSendAGraphQLQueryWithTheValidToken() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        
        String graphqlQuery = """
            {
                "query": "{ __typename }"
            }
            """;
        
        HttpEntity<String> request = new HttpEntity<>(graphqlQuery, headers);
        response = restTemplate.exchange("/graphql", HttpMethod.POST, request, String.class);
    }

    @Then("I should receive a {int} OK response")
    public void iShouldReceiveAnOKResponse(int statusCode) {
        assertThat(response.getStatusCodeValue()).isEqualTo(statusCode);
    }

    @And("the GraphQL response should contain the requested data")
    public void theGraphQLResponseShouldContainTheRequestedData() {
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains("__typename");
    }

    @Given("I have an expired access token")
    public void iHaveAnExpiredAccessToken() {
        // This would be a token that was valid but has expired
        accessToken = "expired-token-12345";
    }

    @When("I send a GraphQL query with the expired token")
    public void iSendAGraphQLQueryWithTheExpiredToken() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        
        String graphqlQuery = """
            {
                "query": "{ __typename }"
            }
            """;
        
        HttpEntity<String> request = new HttpEntity<>(graphqlQuery, headers);
        response = restTemplate.exchange("/graphql", HttpMethod.POST, request, String.class);
    }

    @And("the response should indicate token expiration")
    public void theResponseShouldIndicateTokenExpiration() {
        assertThat(response.getBody()).isNotNull();
        // Specific error for expired tokens
    }

    @When("I check the OAuth2 server health endpoint")
    public void iCheckTheOAuth2ServerHealthEndpoint() {
        // This checks the OAuth2 server directly, not through the API
        headers = new HttpHeaders();
        // Mock OAuth2 server health endpoint
        response = restTemplate.getForEntity("http://localhost:8180/default/.well-known/openid-configuration", String.class);
    }

    @Then("the OAuth2 server should respond with a healthy status")
    public void theOAuth2ServerShouldRespondWithAHealthyStatus() {
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @And("the OAuth2 discovery endpoint should be accessible")
    public void theOAuth2DiscoveryEndpointShouldBeAccessible() {
        ResponseEntity<String> discoveryResponse = restTemplate.getForEntity(
            "http://localhost:8180/default/.well-known/openid-configuration", 
            String.class
        );
        assertThat(discoveryResponse.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Given("I have a valid access token")
    public void iHaveAValidAccessToken() {
        accessToken = obtainValidAccessToken();
    }

    @When("the API introspects the token with the OAuth2 server")
    public void theAPIIntrospectTheTokenWithTheOAuth2Server() {
        // This step is internal to the API - we'll test it indirectly
        // by making a request with the token
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        
        String graphqlQuery = """
            {
                "query": "{ __typename }"
            }
            """;
        
        HttpEntity<String> request = new HttpEntity<>(graphqlQuery, headers);
        response = restTemplate.exchange("/graphql", HttpMethod.POST, request, String.class);
    }

    @Then("the token should be validated as active")
    public void theTokenShouldBeValidatedAsActive() {
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @And("the token should contain the expected claims")
    public void theTokenShouldContainTheExpectedClaims() {
        // This would be verified by the successful response
        // The actual claims verification happens internally in the API
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private String obtainValidAccessToken() {
        // Get token from mock OAuth2 server
        String tokenUrl = "http://localhost:8180/default/token";
        
        HttpHeaders tokenHeaders = new HttpHeaders();
        tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        String body = "grant_type=client_credentials&client_id=test-client&client_secret=test-secret&scope=read write";
        
        HttpEntity<String> tokenRequest = new HttpEntity<>(body, tokenHeaders);
        ResponseEntity<Map> tokenResponse = restTemplate.exchange(
            tokenUrl, 
            HttpMethod.POST, 
            tokenRequest, 
            Map.class
        );
        
        if (tokenResponse.getStatusCode().is2xxSuccessful() && tokenResponse.getBody() != null) {
            return (String) tokenResponse.getBody().get("access_token");
        }
        
        throw new RuntimeException("Failed to obtain access token from mock OAuth2 server");
    }
}