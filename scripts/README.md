# Development Scripts

This directory contains scripts to help maintain code quality and ensure CI/CD compatibility.

## Available Scripts

### validate-pipeline.sh
Performs comprehensive validation of the codebase to ensure it will pass CI/CD checks.

```bash
./scripts/validate-pipeline.sh
```

This script checks:
- Project structure and file existence
- Test file completeness
- Docker configuration
- Application configuration
- Code quality (imports, tags, etc.)

### install-hooks.sh
Installs git pre-commit hooks to catch issues before they reach CI/CD.

```bash
./scripts/install-hooks.sh
```

## Pre-commit Validation

After running `install-hooks.sh`, the following checks will run automatically before each commit:

1. **BDD Tag Validation**: Ensures all `.feature` files have the `@bdd` tag
2. **Import Validation**: Prevents wildcard imports (except static imports)
3. **Pipeline Validation**: Runs the full validation script
4. **Package Declaration**: Ensures all Java files have proper package declarations

### Bypassing Pre-commit Hooks

If you need to commit without running validations (not recommended):

```bash
git commit --no-verify -m "your message"
```

## CI/CD Integration

These scripts mirror the checks performed in the CI/CD pipeline, helping developers catch issues early and avoid failed builds.

### Common Issues and Solutions

1. **Missing @bdd tag**: Add `@bdd` to the top of your `.feature` files
2. **Wildcard imports**: Replace `import package.*` with specific imports
3. **Missing files**: Ensure all required configuration files exist
4. **Failed validation**: Run `./scripts/validate-pipeline.sh` for detailed error messages

## Contributing

When adding new validation requirements:
1. Update `validate-pipeline.sh` with the new check
2. Update the pre-commit hook in `install-hooks.sh`
3. Document the new requirement in this README