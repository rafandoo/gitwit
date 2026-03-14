# Lint

O comando **lint** valida as mensagens de commit utilizando as regras configuradas no GitWit.

Essa validação ajuda a manter um histórico de commits consistente, garantindo que todas as mensagens sigam o padrão definido pelo projeto e permaneçam compatíveis com funcionalidades como geração automática de changelog.

## Exemplo de uso

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

## O que acontece

Durante a execução do workflow, a action irá:

1. Detectar automaticamente o intervalo de commits do Pull Request;
2. Executar o GitWit dentro do ambiente da action;
3. Validar todas as mensagens de commit encontradas;
4. Falhar o workflow caso algum commit não atenda às regras de linting configuradas.

## Exemplo de erro

Se uma mensagem de commit não seguir o padrão esperado, o workflow será interrompido e exibirá um erro detalhado indicando quais commits violaram as regras, como no exemplo abaixo:

```txt
ERROR: following violations were found:
 - 835696e55db9d29b362f61a39ff4f653cec6ffe7:
    - Commit type: The specified commit type is not allowed, check the configuration file. Provided value: invalid.
```

## Boas práticas

Recomenda-se utilizar o `lint` em:

- Pull Requests;
- pipelines de integração contínua (CI);
- validações automáticas antes do merge.

Isso garante que os commits do projeto permaneçam padronizados e compatíveis com o fluxo de automação definido.
