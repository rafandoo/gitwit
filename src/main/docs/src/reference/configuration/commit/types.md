# Commit types

Defines which types of commits can be used and the description associated with each one.
These types are used during the creation and validation of commits, helping to standardize messages and better organize
project history.

```yaml
types:
  description: "Select the type of change you are making"
  values:
    feat: "A new feature"
    fix: "Fixing a bug"
    docs: "Changes in documentation"
```

| Field         | Required | Type   | Description                                                                                                                                                              |
|---------------|----------|--------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `description` | No       | String | Text displayed as an instruction or auxiliary message in the interactive wizard (_wizard_), guiding the user in choosing the type of commit.                             |
| `values`      | Yes      | Map    | List of allowed commit types, where the key represents the type identifier (e.g. `feat`, `fix`) and the value is the description displayed to the user during selection. |

<br>

::: tip 💡 Good practices:

- Use short names and goals (preferably up to 10 characters).
- Keep the types consistent among all project contributors.
- Prefer terms that are widely recognized, such as `feat`, `fix`, `docs`, `test` and `refactor`, to make it easier to
  read the history of commits.
:::
