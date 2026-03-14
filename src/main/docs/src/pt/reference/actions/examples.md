# Exemplos

Esta página apresenta exemplos práticos de uso do **GitWit Action** em diferentes cenários de CI/CD.

Todos os exemplos assumem que o repositório possui o GitWit configurado corretamente.

## ✅ Lint em Pull Requests (CI básico)

Valida automaticamente os commits enviados em Pull Requests.

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

## 🚀 Release automático com Changelog

Gera changelog e cria um GitHub Release automaticamente ao criar uma tag.

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

## 📝 Atualizar automaticamente o CHANGELOG.md

Atualiza o arquivo CHANGELOG.md e faz commit da alteração.

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

## ➕ Acrescentar ao changelog (append)

Adiciona novas entradas sem sobrescrever o arquivo existente.

```yaml
- name: Append changelog
  uses: rafandoo/gitwit-action@v1
  with:
    command: changelog
    args: --append
```

## 📤 Usar changelog como output

Permite reutilizar o changelog em outros steps.

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
