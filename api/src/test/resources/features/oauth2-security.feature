@bdd
Feature: OAuth2 Security for GraphQL API
  As a system administrator
  I want the GraphQL API to be protected by OAuth2
  So that only authorized users can access the API

  Background:
    Given the OAuth2 server is running locally via docker-compose
    And the GraphQL API is configured with OAuth2 security

  Scenario: Unauthenticated request to GraphQL endpoint is rejected
    When I send a GraphQL query without an access token
    Then I should receive a 401 Unauthorized response
    And the response should contain an authentication error

  Scenario: Request with invalid token is rejected
    Given I have an invalid access token
    When I send a GraphQL query with the invalid token
    Then I should receive a 401 Unauthorized response
    And the response should indicate token validation failure

  Scenario: Request with valid token is accepted
    Given I have obtained a valid access token from the OAuth2 server
    When I send a GraphQL query with the valid token
    Then I should receive a 200 OK response
    And the GraphQL response should contain the requested data

  Scenario: Token expiration is handled correctly
    Given I have an expired access token
    When I send a GraphQL query with the expired token
    Then I should receive a 401 Unauthorized response
    And the response should indicate token expiration

  Scenario: OAuth2 server is accessible locally
    When I check the OAuth2 server health endpoint
    Then the OAuth2 server should respond with a healthy status
    And the OAuth2 discovery endpoint should be accessible

  Scenario: Token introspection works correctly
    Given I have a valid access token
    When the API introspects the token with the OAuth2 server
    Then the token should be validated as active
    And the token should contain the expected claims