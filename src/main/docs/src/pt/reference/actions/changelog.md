# Changelog

O comando **changelog** gera automaticamente um changelog a partir das mensagens de commit do repositório.

Dependendo da configuração utilizada no workflow, o changelog pode:

- atualizar ou criar o arquivo `CHANGELOG.md`;
- ser enviado para stdout para uso em outros steps;
- considerar apenas commits desde o último release.

## Exemplo completo de release

O exemplo abaixo demonstra um workflow que gera o changelog automaticamente ao criar uma tag e utiliza o resultado como descrição de um **GitHub Release**.

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

## Gerar changelog em arquivo

```yaml
- name: Generate Changelog
  uses: rafandoo/gitwit-action@v1
  with:
    command: changelog
```

Nesse caso, o GitWit irá gerar ou atualizar o arquivo `CHANGELOG.md` no repositório.

## Acrescentar ao changelog

```yaml
- name: Generate Changelog
  uses: rafandoo/gitwit-action@v1
  with:
    command: changelog
    args: --append
```

Essa configuração adiciona o novo conteúdo ao arquivo `CHANGELOG.md` existente, em vez de sobrescrevê-lo.

## Gerar changelog desde o último release

```yaml
changelog_from_latest_release: true
```

Quando essa opção está habilitada, apenas os commits realizados após o último release serão considerados na geração do changelog.

## Usando o output

Quando `changelog_stdout=true` está configurado, o conteúdo do changelog gerado fica disponível como output da action:

```yaml
${{ steps.gitwit.outputs.changelog }}
```

Para acessar esse output em outros steps, a etapa que executa o GitWit precisa ter um `id` definido (no exemplo acima, `id: gitwit`). Assim, você pode utilizar o changelog gerado em:

- criação de **GitHub Releases**;
- envio de notificações para **Slack**;
- comentários automáticos em **Pull Requests**.
