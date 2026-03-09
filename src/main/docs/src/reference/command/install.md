# Install

Installs GitWit in the current repository or configure its use globally, allowing you to execute commands on
any Git repository.

The installation can be done as an alias of Git or as an automatic hook for direct integration into the commit flow.

## Use

```bash
gitwit install [-fg] [-hk]
```

| Option         | Description                                                                                                                                                |
|----------------|------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `-hk, --hook`  | Installs GitWit as a `prepare-commit-msg` hook in the current repository, allowing you to automatically integrate GitWit into the commit creation process. |
| `-g, --global` | Installs GitWit as a global alias of Git, making the command available in all user repositories.                                                           |
| `-f, --force`  | Forces installation by overwriting an existing hook if it is already configured in the repository.                                                         |

<br>

::: warning ⚠️ Warning:
The `--hook` and `--global` options cannot be used at the same time.
:::

## Examples

```bash
# Installs in the current repository as a Git alias
gitwit install

# Installs globally as a Git alias
gitwit install --global

# Installs GitWit as a hook in the current repository
gitwit install --hook
```
