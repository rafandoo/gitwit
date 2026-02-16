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
gitwit lint [-m=<message>] [<revSpec>]
```

| Option                    | Description                                                                                    |
|---------------------------|------------------------------------------------------------------------------------------------|
| `-m, --message=<message>` | Message to be validated.                                                                       |
| `<revSpec>`               | Git revision specification. It can be a commit, branch, tag or range in the format `from..to`. |

#### Examples:

```bash
# Validate only the most recent commit (default)
gitwit lint

# Validate a specific commit
gitwit lint 105564ac5c6ca88bee5f3f4978287f5c8f87c07b

# Validate a range of commits
gitwit lint 8d2094..105564a

# Validating a message without a commit reference
gitwit lint -m 'feat(ui): Add dark theme'
```

## 📜 `changelog`

Generates a structured changelog from the commit messages of the Git repository, with support for tags, review intervals and automatic version increment.

#### Usage:

```bash
gitwit changelog [[-c] [-s=<subtitle>] [-a] [-l | --for-tag=<forTag>] [-M | -m | -p]] [<revSpec>]
```

| Option               | Description                                                                                        |
|----------------------|----------------------------------------------------------------------------------------------------|
| `-c, --copy`         | Copy the generated changelog to the clipboard.                                                     |
| `-s, --subtitle`     | Sets a subtitle to be displayed in the changelog.                                                  |
| `-a, --append`       | Attach the changelog to the existing file instead of overwriting it.                               |          
| `-l, --last-tag`     | Use the last tag of the repository as a starting point to generate the changelog.                  |
| `--for-tag=<forTag>` | Use the specified tag as start point to generate changelog.                                        |
| `-M --major`         | Increases the **major** version from the last tag and generates the changelog for the new version. |
| `-m, --minor`        | Increases the **minor** version from the last tag and generates the changelog for the new version. |
| `-p, --patch`        | Increases the **patch** version from the last tag and generates the changelog for the new version. |
| `<revSpec>`          | Git revision specification. It can be a commit, branch, tag, or interval in the `from..to`.        |

<br>

::: warning ⚠️ Warning:
The version increment options (-M, -m, -p) are mutually exclusive. Also, the tag options (-l, --for-tag) cannot be used together.
:::

#### Examples:

```bash
# Generates changelog for the last tag
gitwit changelog -l

# Generates the changelog from a specific tag
gitwit changelog --for-tag=v1.2.0

# Generates the changelog by increasing the minor version
gitwit changelog -m

# Generates the changelog for a commit interval
gitwit changelog 8d2094..105564a

# Generates the changelog and copies it to the clipboard
gitwit changelog -l --copy
```
