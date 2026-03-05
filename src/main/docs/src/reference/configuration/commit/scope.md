# Commit Scope

Defines how the scope of the commit should be used, allowing you to control whether it is optional, required, or
restricted to a list of predefined values.

The scope typically represents the project area affected by the change, such as specific modules, components, or
features.

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

| Field         | Required    | Type    | Standard                                                                     | Description                                                                                                                |
|---------------|-------------|---------|------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------|
| `description` | No          | String  | -                                                                            | Text displayed as auxiliary instruction in the interactive wizard (_wizard_), guiding the user on what to report as scope. |
| `required`    | No          | Boolean | false                                                                        | Defines whether the user must obligatorily report a scope when creating the commit.                                        |
| `type`        | Yes         | String  | text                                                                         | Sets the format of the scope field. Can be `text` (free entry) or `list` (selection from predefined values).               |
| `values`      | Conditional | List  - | List of allowed scopes. This field is required when `type` is set to `list`. |

<br>

::: warning ⚠️ Warning:
When `required: true` and `type: list`, the user must select one of the defined scopes to continue creating the commit.
:::
