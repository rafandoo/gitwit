# Changelog

The **changelog** command automatically generates a changelog from the repository’s commit messages.

Depending on the configuration used in the workflow, the changelog can:

- update or create the `CHANGELOG.md` file;
- be sent to stdout for use in other steps;
- consider only commits since the last release.

## Full release example

The example below demonstrates a workflow that generates changelog automatically when creating a tag and uses the result as a description of a **GitHub Release**.

```yaml
name: Release Deployment

on:
  push:
    tags:
      - 'v*'

jobs:
  release:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v6
        with:
          fetch-depth: 0
          ref: main

      - name: Generate Changelog
        id: gitwit
        uses: rafandoo/gitwit-action@v1
        with:
          command: changelog
          changelog_stdout: true
          changelog_from_latest_release: true

      - name: Create GitHub Release
        uses: softprops/action-gh-release@v2
        with:
          body: |
            ${{ steps.gitwit.outputs.changelog }}
```

## Generate changelog in file

```yaml
- name: Generate Changelog
  uses: rafandoo/gitwit-action@v1
  with:
    command: changelog
```

In this case, GitWit will generate or update the `CHANGELOG.md` file in the repository.

## Add to changelog

```yaml
- name: Generate Changelog
  uses: rafandoo/gitwit-action@v1
  with:
    command: changelog
    args: --append
```

This setting adds new content to the existing `CHANGELOG.md` file instead of overwriting it.

## Generate changelog since last release

```yaml
changelog_from_latest_release: true
```

When this option is enabled, only commits made after the last release will be considered in changelog generation.

## Using the output

When `changelog_stdout=true` is configured, the content of the generated changelog becomes available as the action’s output:

```yaml
${{ steps.gitwit.outputs.changelog }}
```

To access this output in other steps, the step that executes GitWit must have an `id` defined (in the example above, `id: gitwit`). So you can use the changelog generated in:

- creation of **GitHub Releases**;
- sending notifications to **Slack**;
- automatic comments on **Pull Requests**.
