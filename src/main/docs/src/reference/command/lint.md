# Lint

Validates commit messages based on the rules configured in the `.gitwit` file, ensuring that commits follow the default
defined by the project.

The command can be used to validate existing commits in the repository or a manually informed message, useful for local
use as well as in automations and pipelines.

The `lint` can validate:

- a specific commit;
- a commit interval;
- or, by default, the most recent commit (HEAD).

## Use

```bash
gitwit lint [-m=<message>] [<revSpec>]
```

| Option                    | Description                                                                                                                                 |
|---------------------------|---------------------------------------------------------------------------------------------------------------------------------------------|
| `-m, --message=<message>` | Validates a directly informed commit message without needing to reference a repository commit.                                              |
| `<revSpec>`               | Git revision specification used to select commits to validate. It can be a commit, branch, tag hash or an interval in the `from..to` format |

## Examples

```bash
# Validates only the most recent commit (default behavior)
gitwit lint

# Validates a specific commit by hash
gitwit lint 105564ac5c6ca88bee5f3f4978287f5c8f87c07b

# Validates a commit interval
gitwit lint 8d2094..105564a

# Validates a message without reference to a commit
gitwit lint -m 'feat(ui): Add dark theme'
```
