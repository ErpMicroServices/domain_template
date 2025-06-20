#!/bin/bash

# Script to install git hooks for the project

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
HOOKS_DIR="$PROJECT_ROOT/.git/hooks"

echo "Installing git hooks..."

# Create pre-commit hook
cat > "$HOOKS_DIR/pre-commit" << 'EOF'
#!/bin/bash

# Pre-commit hook to validate code before committing

echo "Running pre-commit validation..."

# Function to check validation
check_validation() {
    local check_name="$1"
    local command="$2"
    
    if eval "$command"; then
        echo "✅ $check_name: PASSED"
        return 0
    else
        echo "❌ $check_name: FAILED"
        return 1
    fi
}

FAILED=false

# Check for @bdd tags in feature files
if ls api/src/test/resources/features/*.feature 2>/dev/null | head -1 > /dev/null; then
    for feature_file in api/src/test/resources/features/*.feature; do
        if ! grep -q "@bdd" "$feature_file"; then
            echo "❌ Missing @bdd tag in $feature_file"
            FAILED=true
        fi
    done
fi

# Check for wildcard imports (excluding static imports)
if grep -r "import [^static].*\*;" api/src --include='*.java' 2>/dev/null; then
    echo "❌ Found wildcard imports (non-static)"
    FAILED=true
fi

# Check if validation script exists and run it
if [ -f "./scripts/validate-pipeline.sh" ]; then
    echo "Running pipeline validation..."
    if ! ./scripts/validate-pipeline.sh > /dev/null 2>&1; then
        echo "❌ Pipeline validation failed"
        echo "Run ./scripts/validate-pipeline.sh for details"
        FAILED=true
    else
        echo "✅ Pipeline validation: PASSED"
    fi
fi

# Check for proper package structure
if find api/src -name "*.java" -type f | while read file; do
    package_line=$(grep "^package " "$file" | head -1)
    if [ -z "$package_line" ]; then
        echo "❌ Missing package declaration in $file"
        exit 1
    fi
done; then
    echo "✅ Package declarations: PASSED"
else
    FAILED=true
fi

if [ "$FAILED" = true ]; then
    echo ""
    echo "❌ Pre-commit validation FAILED"
    echo "Please fix the issues above and try again."
    exit 1
fi

echo ""
echo "✅ Pre-commit validation PASSED"
exit 0
EOF

chmod +x "$HOOKS_DIR/pre-commit"

echo "✅ Git hooks installed successfully!"
echo ""
echo "The following hooks are now active:"
echo "  - pre-commit: Validates code before committing"
echo ""
echo "To bypass hooks temporarily, use: git commit --no-verify"