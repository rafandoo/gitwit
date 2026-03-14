# Visão geral

O [**GitWit Action**](https://github.com/rafandoo/gitwit-action) permite executar o GitWit diretamente no GitHub Actions, possibilitando validar mensagens de commit e gerar changelogs automaticamente durante a execução de workflows.

A action executa o GitWit dentro de um container Docker, garantindo um ambiente consistente e podendo ser utilizada em qualquer pipeline compatível com GitHub Actions.

Atualmente, a action suporta dois comandos principais:

- **lint** — valida mensagens de commit conforme as regras definidas no projeto;
- **changelog** — gera um changelog a partir do histórico de commits.

## Exemplo básico

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

::: warning ⚠️ Aviso:
`fetch-depth: 0` é necessário para que o GitWit tenha acesso ao histórico completo de commits e consiga realizar a validação corretamente.
:::

## Inputs

| Input                           | Descrição                                                                              | Obrigatório | Padrão  |
|---------------------------------|----------------------------------------------------------------------------------------|:-----------:|:-------:|
| `command`                       | Define qual comando do GitWit será executado (`lint` ou `changelog`)                   |      ✔      |    —    |
| `changelog_stdout`              | Envia o changelog gerado para a saída padrão (_stdout_) em vez de salvá-lo em arquivo. |      ✖      | `false` |
| `changelog_from_latest_release` | Limita o changelog gerado aos commits desde o último release do repositório.           |      ✖      | `false` |
| `args`                          | Argumentos adicionais repassados diretamente para o comando do GitWit.                 |      ✖      |    —    |

## Outputs

| Output      | Descrição                                                                       |
|-------------|---------------------------------------------------------------------------------|
| `changelog` | Conteúdo do changelog gerado (disponível apenas quando `changelog_stdout=true`) |

## Quando usar

Utilize o GitWit Action para:

- validar mensagens de commit em Pull Requests;
- gerar changelogs automaticamente durante o CI;
- automatizar a criação de GitHub Releases;
- manter um histórico de mudanças consistente e padronizado.
