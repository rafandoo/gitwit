# GitWit Configuration

GitWit uses a configuration file called `.gitwit`, located in the **project root**
directory — the same directory where the `.git` directory is located.

This file defines:

- Validation rules for commits.
- Required or optional fields.
- Allowed scopes and types.
- Formatting for commits and changelogs.

This page documents all available keys and how to use them, with examples and default values.

## 1. Basic File Structure

The configuration is organized into sections, each with a specific purpose.
The configuration file uses YAML format to define properties.

::: details 📃 Generate Example Configuration
You can automatically generate an example file by running:

```bash
gitwit --config-example
```

This command will create a `.gitwit` file in the current directory, pre-populated with default values,
ready to be adjusted to your needs.
:::

Basic example:

```yaml
types:
  description: "Select the change type"
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
  title: "Change history"
  types:
    feat: "Features"
    fix: "Fixes"
  showBreakingChanges: true
```

## 2. `types` - Commit types

Defines **which commit types are allowed** and a description for each.

```yaml
types:
  description: "Select the type of change you are making"
  values:
    feat: "A new feature"
    fix: "Bug fix"
    docs: "Documentation changes"
```

| Field         | Required | Type   | Description                                            |
|---------------|----------|--------|--------------------------------------------------------|
| `description` | No       | String | Helper text to display in the wizard                   |
| `values`      | Yes      | Map    | Allowed commit types and their respective descriptions |

<br>

::: tip 💡 Best practices:

Use short names (max. 10 characters) for types.

Prefer recognized terms, such as feat, fix, docs, test, refactor.
:::

## 2. `scope` - Commit Scope

Controls the use of the commit scope: whether it is required, free, or limited to a list.

```yaml
scope:
  description: "Specify the scope of the change"
  required: false
  type: list
  values:
    - core
    - cli
    - api
```

| Field         | Required    | Type    | Default | Description                                                     |
|---------------|-------------|---------|---------|-----------------------------------------------------------------|
| `description` | No          | String  | -       | Auxiliary text to display in the wizard                         |
| `required`    | No          | Boolean | false   | Defines whether the scope is required                           |
| `type`        | Yes         | String  | text    | Can be `text` (free field) or `list` (choose from fixed values) |
| `values`      | Conditional | List    | -       | List of valid scopes (required when `type: list`)               |

<br>

::: warning ⚠️ Warning:
If `required: true` and `type: list`, the user will be required to choose one of the defined scopes.
:::

## 3. `shortDescription` - Short commit description

Defines rules for the mandatory commit short description field.

```yaml
shortDescription:
  description: "Provide a brief summary"
  minLength: 5
  maxLength: 70
```

| Field         | Required | Type   | Default | Description                          |
|---------------|----------|--------|---------|--------------------------------------|
| `description` | No       | String | -       | Helper text to display in the wizard |
| `minLength`   | No       | Int    | 1       | Minimum characters allowed           |
| `maxLength`   | No       | Int    | 72      | Maximum characters allowed           |

## 4. `longDescription` - Long commit description

Enables an additional field for commit details.

```yaml
longDescription:
  enabled: true
  description: "Additional details"
  required: false
  minLength: 20
  maxLength: 100
```

| Field         | Required | Type    | Default | Description                             |
|---------------|----------|---------|---------|-----------------------------------------|
| `enabled`     | No       | Boolean | false   | Enables the long description field      |
| `description` | No       | String  | -       | Auxiliary text to display in the wizard |
| `required`    | No       | Boolean | false   | Defines whether it is required          |
| `minLength`   | No       | Int     | 0       | Minimum characters allowed              |
| `maxLength`   | No       | Int     | 100     | Maximum characters allowed              |

## 5. `breakingChanges` - Critical Changes

Allows you to highlight changes that break compatibility.

```yaml
breakingChanges:
  enabled: true
  description: "List significant changes"
```

| Field         | Required | Type    | Default | Description                             |
|---------------|----------|---------|---------|-----------------------------------------|
| `enabled`     | No       | Boolean | false   | Enables the field for breaking changes  |
| `description` | No       | String  | -       | Auxiliary text to display in the wizard |

## 6. `changelog` - Changelog Generation

Configures how the changelog will be compiled.

```yaml
changelog:
  title: "Changelog"
  types:
    feat: "New features"
    fix: "Fixes"
  showOtherTypes: true
  showBreakingChanges: true
  ignored:
    - chore
```

| Field                 | Required | Type    | Default     | Description                                      |
|-----------------------|----------|---------|-------------|--------------------------------------------------|
| `title`               | No       | String  | "Changelog" | Title used at the top of the generated changelog |
| `types`               | Yes      | Map     | -           | Maps commit types to changelog sections          |
| `showOtherTypes`      | No       | Boolean | true        | Shows types not listed in `types`                |
| `showBreakingChanges` | No       | Boolean | false       | Includes breaking changes section                |
| `ignored`             | No       | List    | -           | Commit types ignored in changelog generation     |

## 7. `changelog.format` - Display Templates

Controls the display template for commits in the changelog.

```yaml
format:
  sectionTemplate: "{scope}: {description} ({shortHash})"
  breakingChangesTemplate: "{type} ({scope})!: {description} ({shortHash})"
  otherTypesTemplate: "{type} ({scope}): {description} ({shortHash})"
  defaultTemplate: "{type}: {description}"
```

| Field                     | Required | Type   | Description                           |
|---------------------------|----------|--------|---------------------------------------|
| `sectionTemplate`         | No       | String | Template for types listed in `types`  |
| `breakingChangesTemplate` | No       | String | Template specific to breaking changes |
| `otherTypesTemplate`      | No       | String | Template for unlisted types           |
| `defaultTemplate`         | No       | String | Generic template as fallback          |

Supported template variables:

- `{type}`: Commit type (feat, fix, etc.)
- `{scope}`: Defined scope
- `{description}`: Short description
- `{hash}`: Full commit hash
- `{shortHash}`: First 7 characters of the hash
- `{breakingChanges}`: Breaking change tag
- `{author}`: Commit author
- `{date}`: Date and time of the commit
