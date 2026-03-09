# Linting rules

Define additional rules used during validation of commit messages.
These settings allow you to automatically ignore specific commits in the linting process, preventing auto-generated or
auxiliary commits from being validated.

Example configuration:

```yaml
lint:
  ignored:
    - Merge
    - Pull request
```

| Field     | Required | Type | Default                                                | Description                                                                                                                                                                                      |
|-----------|----------|------|--------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `ignored` | No       | List | `Merge`, `Revert`, `Pull request`, `fixup!`, `squash!` | List of patterns applied to commit messages during linting. If the commit message contains any of the defined values, the commit will be ignored and won’t pass through the validations of lint. |
