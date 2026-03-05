# GitWit Configuration

GitWit uses a configuration file called `.gitwit`, located in the directory
**root of the project** - same as where the `.git`directory is located.

This file defines:

- Validation rules for commits.
- Required or optional fields.
- Permissible scopes and types.
- Formatting for commits and changelogs.

This page documents all available keys and how to use them, with examples and default values.

## Basic file structure

The configuration is organized into sections, each with a specific purpose.
The configuration file uses YAML format for setting properties

::: details 📃 Generate sample configuration
You can automatically generate a sample file by running:

```bash
gitwit --config-example
```

This command will create in the current directory a `.gitwit` prefilled with default values,
ready to be adjusted according to your needs.
:::

Basic example:

```yaml
types:
  description: "Select the type of change"
  values:
    feat: "New feature"
    fix: "Bug fix"

scope:
  type: list
  values:
    - core
    - api
    - cli

shortDescription:
  minLength: 5
  maxLength: 70

changelog:
  title: "History of changes"
  types:
    feat: "Features"
    fix: "Corrections"
  showBreakingChanges: true
```

## Configuration examples

Next, you will find two ready-to-use templates, each with its own style:

- [Basic example](./examples/basic-example.md) - ideal for beginners or those who want to keep the standard of simple
  and objective commits.
- [Example with Emojis](./examples/emoji-example.md) - perfect for anyone who wants to add expressiveness and make it
  easier to visually identify commits in the history.

Use what best fits your workflow - or combine ideas to create your own custom setup.
