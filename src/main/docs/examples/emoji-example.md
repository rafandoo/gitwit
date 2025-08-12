# Example configuration with emojis

In addition to the traditional Conventional Commits format, there is a popular approach that uses emojis to classify and add more expressiveness to commit messages.

This practice makes the history easier to read and more visual, allowing you to quickly identify the type of change made—even without reading the entire description.

One of the best-known conventions is [Gitmoji](https://gitmoji.dev/), which maintains a standardized list of emojis and their respective meanings.

GitWit fully supports this style and allows for easy configuration.

Below is an example YAML configuration file for using GitWit with emojis:

```yaml
types:
  description: "Select the type of change you are making"
  values:
    ✨: "A new feature"
    🐛: "Fixing a bug"
    📝: "Documentation changes only"
    ♻️: "Code changes that don't fix a bug or add a feature"
    ✅: "Adding or fixing tests"
    🧹: "Other changes that don't modify src or test files"
    📦: "Changes that affect the build system or external dependencies"
    ⬆️: "Dependency updates"
    ⬇️: "Dependency downgrading"
    🔥: "Removing code or files"
    🔒: "Changes Security-related"
    🌐: "Internationalization or localization"

scope:
  description: "Specify the scope of the change (e.g., component, module, etc.)"
  required: false
  type: text

shortDescription:
  description: "Provide a brief descriptive summary of the change"
  required: true
  minLength: 5
  maxLength: 70

changelog:
  title: "Changelog"
  types:
    ✨: "New features"
    🐛: "Bug fixes"
    ♻️: "Code refactorings"
    📝: "Documentation updates"
    ✅: "Tests added or fixed"
    🔒: "Security fixes"
  showOtherTypes: true
  ignored:
    - 🧹
  format:
    sectionTemplate: "{scope}: {description} ({shortHash})" 
    breakingChangesTemplate: "{type} ({scope})!: {description} ({shortHash})" 
    otherTypesTemplate: "{type} ({scope}): {description} ({shortHash})" 
    defaultTemplate: "{type}: {description}"
```
