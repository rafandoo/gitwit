# Uninstall

Remove GitWit from the current repository or undo its global installation, restoring Git’s default behavior without the
tool integration.

Removal can affect both configured aliases and previously installed hooks.

## Use

```bash
gitwit uninstall [-g] [-hk]
```

| Option         | Description                                                                       |
|----------------|-----------------------------------------------------------------------------------|
| `-hk, --hook`  | Remove the `prepare-commit-msg` hook configured in the current repository.        |
| `-g, --global` | Remove GitWit global alias, making the command unavailable in other repositories. |

<br>

::: warning ⚠️ Warning:
The `--hook` and `--global` options cannot **be used together.
:::

## Examples

```bash
# Remove the configured alias from the current repository
gitwit uninstall

# Remove GitWit global installation
gitwit uninstall --global

# Remove the current repository hook
gitwit uninstall --hook
```
