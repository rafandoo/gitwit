# Breaking changes

Sets up an optional field to record changes that break compatibility (breaking changes), i.e., changes that may require
adaptations by users or other systems that depend on the project.

When enabled, the user can report these changes during commit creation, allowing them to be highlighted later in tools
like changelog.

```yaml
breakingChanges:
  enabled: true
  description: "List the significant changes"
```

| Field         | Required | Type    | Default | Description                                                                                                                  |
|---------------|----------|---------|---------|------------------------------------------------------------------------------------------------------------------------------|
| `enabled`     | No       | Boolean | false   | Enables or disables the field for breaking changes record during commit creation.                                            |
| `description` | No       | String  | -       | Text displayed as auxiliary instruction in the interactive wizard (_wizard_), guiding the user to describe critical changes. |
