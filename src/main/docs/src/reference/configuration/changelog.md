# Changelog generation

This section defines the settings responsible for automatically generating project changelog from commits history.
Through these options, you can control which commits are included, how they are grouped into sections, and how they
display in the final file.

The changelog is built based on the types of commit configured, allowing you to organize changes such as new features,
fixes and incompatible changes in a standardized and readable way for project users.

Sample configuration for changelog:

```yaml
changelog:
  title: "Changelog"
  filepath: "CHANGELOG.md"
  types:
    feat: "New features"
    fix: "Corrections"
  showOtherTypes: true
  showBreakingChanges: true
  ignored:
    - cry
```

| Field                 | Required | Type    | Standard                                                 | Description                                                                                                                                                                                       |
|-----------------------|----------|---------|----------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `title`               | No       | String  | "Changelog"                                              | Title used at the top of generated changelog.                                                                                                                                                     |
| `filepath`            | No       | String  | "CHANGELOG.md"                                           | File path where the changelog will be created or updated. Can be relative to the project root.                                                                                                    |
| `types`               | Yes      | Map     | -                                                        | Define which types of commits will appear as changelog sections and the displayed name for each section.                                                                                          |
| `showOtherTypes`      | No       | Boolean | true                                                     | When enabled, includes commits whose type is not defined in types, grouping them in a separate section.                                                                                           |
| `showBreakingChanges` | No       | Boolean | false                                                    | Adds a specific section for commits marked as _breaking changes_                                                                                                                                  |
| `ignored`             | No       | List    | `Merge`, `Revert`, `Pull request`, `fixup! `, `squash! ` | List of patterns used to filter commits during changelog generation. If the full commit message contains any of the defined values, the commit will be ignored and won’t appear in the changelog. |

## Display Templates

This section defines how commits will be formatted and presented in the generated changelog.
Through the templates, it is possible to customize the textual structure of each entry, controlling which information
from
commit will be displayed and in what format.

Each template is applied according to the type of commit and the context of the generation (such as common commits,
breaking changes or
types not configured), allowing to adapt the changelog to the standard of project documentation.

Example configuration for display templates:

```yaml
changelog:
  # other settings...

  format:
    sectionTemplate: "{scope}: {description} ({shortHash})"
    breakingChangesTemplate: "{type} ({scope})!: {description} ({shortHash})"
    otherTypesTemplate: "{type} ({scope}): {description} ({shortHash})"
    defaultTemplate: "{type}: {description}"
```

| Field                     | Required | Type   | Description                                                                                    |
|---------------------------|----------|--------|------------------------------------------------------------------------------------------------|
| `sectionTemplate`         | No       | String | Template used for commits whose types are defined in `types`.                                  |
| `breakingChangesTemplate` | No       | String | Template applied exclusively to commits marked as _breaking changes_.                          |
| `otherTypesTemplate`      | No       | String | Template used for commits of types not configured in `types` when `showOtherTypes` is enabled. |
| `defaultTemplate`         | No       | String | Fallback template used if no other template is applicable.                                     |

### Variables available in our models

The following variables can be used in any model:

- `{type}` - commit type (e.g. `feat`, `fix`)
- `{scope}` - informed scope without commitment
- `{description}` - short description of the commit
- `{hash}` - complete commit hash
- `{shortHash}` - short hash version (first 7 characters)
- `{breakingChanges}` - change break indicator
- `{author}` - commit author
- `{date}` - commit date and time
