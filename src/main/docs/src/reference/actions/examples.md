# Examples

This page presents practical examples of using **GitWit Action** in different CI/CD scenarios.

All examples assume that the repository has GitWit configured correctly.

## ✅ Lint in Pull Requests (basic CI)

Automatically validates commits submitted in pull requests.

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

      - name: Lint commits
        uses: rafandoo/gitwit-action@v1
        with:
          command: lint
```

## 🚀 Automatic release with changelog

Generates changelog and creates a GitHub Release automatically when creating a tag.

```yaml
name: Release Deployment

on:
  push:
    tags:
      - 'v*'

jobs:
  release:
    runs-on: ubuntu-latest
    permissions:
      contents: write

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
          tag_name: ${{ github.ref_name }}
          name: ${{ github.ref_name }}
          body: |
            ${{ steps.gitwit.outputs.changelog }}
```

## 📝 Automatically update CHANGELOG.md

Updates the CHANGELOG.md file and commits the change.

```yaml
name: Update Changelog

on:
  push:
    tags:
      - 'v*'

jobs:
  changelog:
    runs-on: ubuntu-latest
    permissions:
      contents: write

    steps:
      - uses: actions/checkout@v6
        with:
          fetch-depth: 0

      - name: Generate changelog
        uses: rafandoo/gitwit-action@v1
        with:
          command: changelog

      - name: Commit changelog
        uses: EndBug/add-and-commit@v9
        with:
          message: "docs(changelog): update release notes"
          default_author: github_actions
```

## ➕ Add to changelog (append)

Adds new entries without overwriting the existing file.

```yaml
- name: Append changelog
  uses: rafandoo/gitwit-action@v1
  with:
    command: changelog
    args: --append
```

## 📤 Use changelog as output

Allows you to reuse the changelog in other steps.

```yaml
- name: Generate changelog
  id: gitwit
  uses: rafandoo/gitwit-action@v1
  with:
    command: changelog
    changelog_stdout: true

- name: Print changelog
  run: echo "${{ steps.gitwit.outputs.changelog }}"
```
