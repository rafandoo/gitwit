# Overview

The [**GitWit Action**](https://github.com/rafandoo/gitwit-action) allows you to run GitWit directly on GitHub Actions, enabling you to validate commit messages and generate changelogs automatically during workflow execution.

The action runs GitWit within a Docker container, ensuring a consistent environment and can be used in any GitHub Actions-enabled pipeline.

Currently, the action supports two main commands:

- **lint** - validates commit messages according to the rules defined in the project;
- **changelog** - generates a changelog from the commit history.

## Basic example

```yaml
name: GitWit CI

on:
  pull_request:
    branches:
      - main

jobs:
  gitwit:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v6
        with:
          fetch-depth: 0

      - name: Run GitWit Lint
        uses: rafandoo/gitwit-action@v1
        with:
          command: lint
```

::: warning ⚠️ Warning:
`fetch-depth: 0` is required for GitWit to have access to the full history of commits and be able to perform validation correctly.
:::

## Inputs

| Input                           | Description                                                                                    | Required | Default |
|---------------------------------|------------------------------------------------------------------------------------------------|:--------:|:-------:|
| `command`                       | Define which GitWit command will be executed (`lint` or `changelog`)                           |    ✔     |    -    |
| `changelog_stdout`              | Sends the generated changelog to the default output (_stdout_) instead of saving it in a file. |    ✖     | `false` |
| `changelog_from_latest_release` | Generates the changelog only from commits since the latest release instead of all commits.     |    ✖     | `false` |
| `args`                          | Additional arguments passed directly to the command of GitWit.                                 |    ✖     |    -    |

## Outputs

| Output      | Description                                                               |
|-------------|---------------------------------------------------------------------------|
| `changelog` | Generated changelog content (available only when `changelog_stdout=true`) |

## When to use

Use GitWit Action to:

- validate commit messages in Pull Requests;
- generate changelogs automatically during CI;
- automate the creation of GitHub Releases;
- maintain a consistent and standardized history of changes.
