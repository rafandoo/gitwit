# Changelog

Generates a structured changelog from the Git repository’s commit messages, automatically arranging changes according to
the settings set in `.gitwit`.

The command allows you to generate changelogs based on tags, commit intervals or specific revisions, and supports
automatic version increment following semantic versioning.

## Use

```bash
gitwit changelog [[-a] [-s=<subtitle> | -n] [-c | -S] [-l | --for-tag=<forTag>] [-M | -m | -p]] [<revSpec>]
```

| Option               | Description                                                                                                                   |
|----------------------|-------------------------------------------------------------------------------------------------------------------------------|
| `-c, --copy`         | Copy the generated changelog to the transfer area instead of just saving it.                                                  |
| `-s, --subtitle`     | Sets a subtitle to be displayed next to the generated changelog.                                                              |
| `-n, --no-subtitle`  | Generates the changelog without including a subtitle.                                                                         |
| `-a, --append`       | Adds the generated content to the existing changelog file instead of overwriting it.                                          |
| `-S, --stdout`       | Displays the changelog directly in the terminal, without saving it to a file.                                                 |
| `-l, --last-tag`     | Uses the last tag of the repository as a starting point for changelog generation.                                             |
| `--for-tag=<forTag>` | Use the informed tag as the starting point for changelog generation.                                                          |
| `-M, --major`        | Increases the **major** version from the last tag and generates the changelog for the new version.                            |
| `-m, --minor`        | Increases the version **minor** from the last tag and generates the changelog for the new version.                            |
| `-p, --patch`        | Increases the version **patch** from the last tag and generates the changelog for the new version.                            |
| `<revSpec>`          | Git revision specification used as a basis for generation. Can be a commit, branch, tag, or interval in the format `from..to` |

<br>

::: warning ⚠️ Warning:
The version increment options (-M, -m, -p) are mutually exclusive.

In addition, the following groups of options may not be used simultaneously:

- `-l, --last-tag` and `--for-tag=<forTag>`
- `-s, --subtitle` and `-n, --no-subtitle`
- `-c, --copy` and `-S, --stdout`
:::

## Examples

```bash
# Generates the changelog using the last tag as a base
gitwit changelog -l

# Generates the changelog from a specific tag
gitwit changelog --for-tag=v1.2.0

# Increments the minor version and generates the corresponding changelog
gitwit changelog -m

# Generates the changelog for a commit interval
gitwit changelog 8d2094..105564a

# Generates the changelog and copies the result to the transfer area
gitwit changelog -l --copy
```
