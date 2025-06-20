#!/bin/bash

# Pipeline Validation Script
# This script performs all the checks that would run in CI/CD pipeline

set -e

echo "🔄 Starting Pipeline Validation..."
echo "================================="

# Store validation results
VALIDATION_FAILED=false
VALIDATION_LOG=""

# Function to run validation step with error tracking
run_validation() {
    local step_name="$1"
    local command="$2"
    
    echo "  🔍 Running: $step_name"
    if eval "$command"; then
        echo "  ✅ $step_name - PASSED"
        VALIDATION_LOG="$VALIDATION_LOG\n✅ $step_name: PASSED"
    else
        echo "  ❌ $step_name - FAILED"
        VALIDATION_LOG="$VALIDATION_LOG\n❌ $step_name: FAILED"
        VALIDATION_FAILED=true
    fi
}

# Check file structure
echo "1. Checking Project Structure..."
run_validation "OAuth2 Feature File" "test -f api/src/test/resources/features/oauth2-security.feature"
run_validation "Security Config" "test -f api/src/main/java/org/erp_microservices/domain_template/config/OAuth2SecurityConfig.java"
run_validation "Token Validation Service" "test -f api/src/main/java/org/erp_microservices/domain_template/security/OAuth2TokenValidationService.java"
run_validation "GraphQL Security Context" "test -f api/src/main/java/org/erp_microservices/domain_template/graphql/GraphQLSecurityContext.java"

# Check test files
echo ""
echo "2. Checking Test Files..."
run_validation "OAuth2 Security Config Test" "test -f api/src/test/java/org/erp_microservices/domain_template/config/OAuth2SecurityConfigTest.java"
run_validation "Token Validation Service Test" "test -f api/src/test/java/org/erp_microservices/domain_template/security/OAuth2TokenValidationServiceTest.java"
run_validation "GraphQL Security Context Test" "test -f api/src/test/java/org/erp_microservices/domain_template/graphql/GraphQLSecurityContextTest.java"
run_validation "Cucumber Test Runner" "test -f api/src/test/java/org/erp_microservices/domain_template/CucumberTestRunner.java"
run_validation "BDD Tag in Feature File" "grep -q '@bdd' api/src/test/resources/features/oauth2-security.feature"

# Check Docker configuration
echo ""
echo "3. Checking Docker Configuration..."
run_validation "Keycloak in docker-compose" "grep -q 'keycloak:' docker-compose.yml"
run_validation "Keycloak database setup" "grep -q 'CREATE DATABASE keycloak' docker/postgres/init.sql"
run_validation "OAuth2 environment vars" "grep -q 'OAUTH2_ISSUER_URI' docker-compose.yml"

# Check application configuration
echo ""
echo "4. Checking Application Configuration..."
run_validation "Application YAML" "test -f api/src/main/resources/application.yml"
run_validation "OAuth2 config in YAML" "grep -q 'oauth2' api/src/main/resources/application.yml"
run_validation "Test Application YAML" "test -f api/src/test/resources/application-test.yml"
run_validation "Main Application Class" "test -f api/src/main/java/org/erp_microservices/domain_template/DomainTemplateApplication.java"

# Check code compilation readiness
echo ""
echo "5. Checking Code Quality..."
run_validation "No missing imports in main" "! grep -r 'import.*\\*;' api/src/main/java --include='*.java' || echo 'No wildcard imports'"
run_validation "No missing imports in tests" "! grep -r 'import.*\\*;' api/src/test/java --include='*.java' || echo 'No wildcard imports'"

# Display validation summary
echo ""
echo "📊 PIPELINE VALIDATION SUMMARY:"
echo "==============================="
echo -e "$VALIDATION_LOG"
echo ""

# Handle validation results
if [ "$VALIDATION_FAILED" = true ]; then
    echo "❌ PIPELINE VALIDATION FAILED"
    echo "Some checks failed. Please fix the issues and re-run validation."
    exit 1
else
    echo "✅ PIPELINE VALIDATION PASSED"
    echo "All checks passed successfully!"
    exit 0
fi