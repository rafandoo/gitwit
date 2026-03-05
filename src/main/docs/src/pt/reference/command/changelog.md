# Changelog

Gera um changelog estruturado a partir das mensagens de commit do repositório Git, organizando automaticamente as
alterações conforme as configurações definidas no `.gitwit`.

O comando permite gerar changelogs com base em tags, intervalos de commits ou revisões específicas, além de oferecer
suporte ao incremento automático de versão seguindo o versionamento semântico.

## Uso

```bash
gitwit changelog [[-a] [-s=<subtitle> | -n] [-c | -S] [-l | --for-tag=<forTag>] [-M | -m | -p]] [<revSpec>]
```

| Opção                | Descrição                                                                                                                             |
|----------------------|---------------------------------------------------------------------------------------------------------------------------------------|
| `-c, --copy`         | Copia o changelog gerado para a área de transferência em vez de apenas salvá-lo.                                                      |
| `-s, --subtitle`     | Define um subtítulo a ser exibido junto ao changelog gerado.                                                                          |
| `-n, --no-subtitle`  | Gera o changelog sem incluir subtítulo.                                                                                               |
| `-a, --append`       | Adiciona o conteúdo gerado ao arquivo de changelog existente em vez de sobrescrevê-lo.                                                |
| `-S, --stdout`       | Exibe o changelog diretamente no terminal, sem salvar em arquivo.                                                                     |
| `-l, --last-tag`     | Utiliza a última tag do repositório como ponto inicial para geração do changelog.                                                     |
| `--for-tag=<forTag>` | Utiliza a tag informada como ponto inicial para geração do changelog.                                                                 |
| `-M --major`         | Incrementa a versão **major** a partir da última tag e gera o changelog para a nova versão.                                           |
| `-m, --minor`        | Incrementa a versão **minor** a partir da última tag e gera o changelog para a nova versão.                                           |
| `-p, --patch`        | Incrementa a versão **patch** a partir da última tag e gera o changelog para a nova versão.                                           |
| `<revSpec>`          | Especificação de revisão do Git utilizada como base para geração. Pode ser um commit, branch, tag ou intervalo no formato `from..to`. |

<br>

::: warning ⚠️ Aviso:
As opções de incremento de versão (-M, -m, -p) são mutuamente exclusivas.

Além disso, os seguintes grupos de opções não podem ser utilizados simultaneamente:

- `-l, --last-tag` e `--for-tag=<forTag>`
- `-s, --subtitle` e `-n, --no-subtitle`
- `-c, --copy` e `-S, --stdout`
:::

## Exemplos

```bash
# Gera o changelog usando a última tag como base
gitwit changelog -l

# Gera o changelog a partir de uma tag específica
gitwit changelog --for-tag=v1.2.0

# Incrementa a versão minor e gera o changelog correspondente
gitwit changelog -m

# Gera o changelog para um intervalo de commits
gitwit changelog 8d2094..105564a

# Gera o changelog e copia o resultado para a área de transferência
gitwit changelog -l --copy
```
