# GitWit Commands

GitWit has commands for installing, uninstalling, creating standardized commits,
validating messages, and generating changelogs.

This page documents each command, its options, and usage examples.

<br>

::: info 💡 Tip:
To see help for any command, run:

```bash
gitwit <command> --help
```

:::

## 🔹 Main Command (`gitwit`)

The main command provides access to all functionality.

#### Usage:

```bash
gitwit [-dhV] [-ce] [COMMAND]
```

| Option                  | Description                                                             |
|-------------------------|-------------------------------------------------------------------------|
| `-d, --debug`           | Enables debug mode, displaying additional information during execution. |
| `-ce, --config-example` | Generates an example `.gitwit` file in the current directory.           |
| `-h, --help`            | Displays general help.                                                  |
| `-V, --version`         | Displays the installed version.                                         |

## ⚙️ `install`

Installs GitWit in the current repository or globally for all repositories.

#### Usage:

```bash
gitwit install [-fg] [-hk]
```

| Option         | Description                                                        |
|----------------|--------------------------------------------------------------------|
| `-hk, --hook`  | Installs as a `prepare-commit-msg` hook in the current repository. |
| `-g, --global` | Installs as a global Git alias, available in all repositories.     |
| `-f, --force`  | Forces the installation, overwriting an existing hook.             |

<br>

::: warning ⚠️ Warning
The --hook and --global options **cannot** be used simultaneously.
:::

#### Examples:

```bash
# Installs in the current repository as a Git alias
gitwit install

# Installs globally as a Git alias
gitwit install --global

# Installs GitWit as a hook in the current repository
gitwit install --hook
```

## ❌ `uninstall`

Removes the GitWit installation from the current repository or globally.

#### Usage:

```bash
gitwit uninstall [-g] [-hk]
```

| Option         | Description                                                        |
|----------------|--------------------------------------------------------------------|
| `-hk, --hook`  | Removes the `prepare-commit-msg` hook from the current repository. |
| `-g, --global` | Removes the global GitWit alias.                                   |

<br>

::: warning ⚠️ Warning
The --hook and --global options cannot be used together.
:::

#### Examples:

```bash
# Removes the alias from the current repository
gitwit uninstall

# Removes the global installation
gitwit uninstall --global

# Removes the hook from the current repository
gitwit uninstall --hook
```

## 📝 `commit`

Launches the interactive commit wizard or allows you to pass data directly via parameters.

#### Usage:

```bash
gitwit commit [-aem] [-t=<type>] [-s=<scope>] [-d=<shortDescription>] [-l=<longDescription>]
```

| Option                                     | Description                                             |
|--------------------------------------------|---------------------------------------------------------|
| `-a, --add`                                | Automatically adds all modified and untracked files.    |
| `-m, --amend`                              | Modifies the last commit instead of creating a new one. |
| `-e, --allow-empty`                        | Allows creating empty commits.                          |
| `-t, --type=<type>`                        | Commit type (`feat`, `fix`, `chore`, etc.).             |
| `-s, --scope=<scope>`                      | Commit scope (`core`, `ui`, `auth`, etc.).              |
| `-d, --description=<shortDescription>`     | Brief description of the commit.                        |
| `-l, --long-description=<longDescription>` | Detailed description of the commit.                     |

#### Examples:

```bash
# Interactive wizard
gitwit commit

# Direct commit with parameters
gitwit commit -t feat -s core -d "adds log support"

# Commit with files already added and modifications to the last commit
gitwit commit --amend --add -t fix -s api -d "fixes JWT authentication"
```

## 🔍 `lint`

Validates commit messages based on the rules defined in `.gitwit`.

The command can validate:

- a specific commit
- a range of commits
- or, by default, the most recent commit (HEAD)

#### Usage:

```bash
gitwit lint [<rev-spec>] [-m=<message>]
```

| Option                    | Description                                                                              |
|---------------------------|------------------------------------------------------------------------------------------|
| `-m, --message=<message>` | Message to be validated.                                                                 |
| `<rev-spec>`              | Git revision specification. It can be a commit, branch, tag or range in the format from. |

#### Examples:

Validate only the most recent commit (default):

```bash
gitwit lint
```

Validate a specific commit:

```bash
gitwit lint 105564ac5c6ca88bee5f3f4978287f5c8f87c07b
```

Validate a range of commits (including):

```bash
gitwit lint 8d2094..105564a
```

Validating a message without a commit reference:

```bash
gitwit lint -m 'feat(ui): Add dark theme'

```

## 📜 `changelog`

Generates a formatted changelog from commit messages.

#### Usage:

```bash
gitwit changelog [-ac] -f=<from> [-t=<to>] [-s=<subtitle>]
```

| Option              | Description                                                      |
|---------------------|------------------------------------------------------------------|
| `-f, --from=<from>` | Starting point (hash, tag, or branch).                           |
| `-t, --to=<to>`     | Ending point. Default: `HEAD`.                                   |
| `-s, --subtitle`    | Subtitle to be displayed in the changelog.                       |
| `-a, --append`      | Indicates whether the changelog will be appended or overwritten. |
| `-c, --copy`        | Copies the generated changelog to the clipboard.                 |

#### Examples:

```bash
# Generate changelog since the last tag
gitwit changelog --from v1.0.0

# Generate changelog and copy to the clipboard
gitwit changelog --from v1.0.0 --to v1.2.0 --copy
```
