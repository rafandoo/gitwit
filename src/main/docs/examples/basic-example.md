# Basic Configuration Example

Below is a basic Git configuration example that can be used as a starting point to customize your workflow:

```yaml
types:
  description: "Select the type of change you are making"
  values:
    feat: "A new feature"
    fix: "Fix a bug"
    docs: "Documentation changes"
    refactor: "Code changes that don't fix a bug or add a feature"
    test: "Add or fix tests"
    chore: "Other changes that don't modify the src or test files"
    build: "Changes that affect the build system or external dependencies"
    bump: "Dependency updates"
    down: "Dependency downgrades"
    remove: "Removal of code or files"
    sec: "Security-related changes"

scope:
  description: "Specify the scope of the change (e.g., component, module, etc.)" etc.)"
  required: true
  type: list
  values:
    - core
    - api
    - cli
    - config

shortDescription:
  description: "Provide a brief descriptive summary of the change"
  required: true
  minLength: 15
  maxLength: 70

longDescription:
  enabled: true
  description: "Additional details about the change, why it was made, and any context"
  required: true
  minLength: 20
  maxLength: 100

changelog:
  title: "Changelog"
  types:
    feat: "New features"
    fix: "Bug fixes"
    refactor: "Code refactorings"
    docs: "Documentation updates"
    test: "Tests added or fixed"
    sec: "Security fixes"
  showOtherTypes: true
  ignored:
    - chore
  format:
    sectionTemplate: "{scope}: {description} ({shortHash})"
    breakingChangesTemplate: "{type} ({scope})!: {description} ({shortHash})"
    otherTypesTemplate: "{type} ({scope}): {description} ({shortHash})"
    defaultTemplate: "{type}: {description}"
```
