# CLAUDE.md - Domain Template Repository

This is a template repository for creating domain-driven microservices. When creating a new project from this template, customize this file to reflect your specific domain and requirements.

## Template Repository Notice

This is a **template repository** designed to provide a foundation for domain-driven microservices. It includes:

- Basic project structure for domain-driven design
- GraphQL API setup with OAuth2 authentication (standalone, no AWS dependencies)
- Testing framework configuration
- CI/CD pipeline templates

## Customizing This Template

When using this template for a new project:

### 1. Update Project Information
Replace the following placeholders throughout the repository:
- `domain_template` → Your actual domain name
- Update package names and module identifiers
- Modify project description and documentation

### 2. Configure Domain-Specific Details
- Define your domain entities and value objects
- Implement domain-specific business logic
- Create appropriate aggregates and repositories
- Design your GraphQL schema for your domain

### 3. Project-Specific Configuration

Add the following sections when customizing for your project:

```markdown
## Project Overview
[Describe your specific domain and its purpose]

## Domain Model
[Document your domain entities, value objects, and aggregates]

## API Endpoints
[List and describe your GraphQL queries and mutations]

## Authentication Configuration
[Detail your OAuth2 setup and provider configuration]

## Development Commands
[List project-specific commands like build, test, lint, etc.]

## Deployment Instructions
[Provide environment-specific deployment guidance]
```

### 4. Technology Stack Customization
Document any changes or additions to the base technology stack:
- Additional dependencies
- Framework configurations
- External service integrations

### 5. Testing Strategy
Define your project-specific testing approach:
- Unit testing conventions
- Integration testing setup
- End-to-end testing requirements

## Base Template Features

This template provides:

- **Domain-Driven Design Structure**: Pre-configured directories for domain logic, application services, and infrastructure
- **GraphQL API**: Basic GraphQL setup with resolvers and schema structure
- **OAuth2 Authentication**: Standalone OAuth2 implementation (no AWS dependencies)
- **Testing Framework**: Unit and integration testing setup
- **CI/CD Pipeline**: GitHub Actions workflows for continuous integration

## Important Notes

- This template uses standalone OAuth2 implementation without AWS services
- All AWS references (Amplify, Cognito, etc.) have been removed
- GraphQL API is designed to work with standard OAuth2 providers

## Getting Started with This Template

1. Click "Use this template" on GitHub
2. Clone your new repository
3. Update this CLAUDE.md file with your project-specific information
4. Follow the customization steps above
5. Remove this template notice section once customization is complete

---

**Remember**: This file should evolve with your project. Keep it updated as your domain model and implementation details change.