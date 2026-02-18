# Comandos do GitWit

O GitWit possui comandos para instalação, desinstalação, criação de commits padronizados,
validação de mensagens e geração de changelogs.

Esta página documenta cada comando, suas opções e exemplos de uso.

<br>

::: info 💡 Dica:
Para ver a ajuda de qualquer comando, execute:

```bash
gitwit <comando> --help
```

:::

## 🔹 Comando principal (`gitwit`)

O comando principal fornece acesso a todas as funcionalidades.

#### Uso:

```bash
gitwit [-dhV] [-ce] [COMMAND]
```

| Opção                   | Descrição                                                                      |
|-------------------------|--------------------------------------------------------------------------------|
| `-d, --debug`           | Ativa o modo de depuração, exibindo informações adicionais durante a execução. |
| `-ce, --config-example` | Gera um arquivo `.gitwit` de exemplo no diretório atual.                       |
| `-h, --help`            | Exibe a ajuda geral.                                                           |
| `-V, --version`         | Mostra a versão instalada.                                                     |

## ⚙️ `install`

Instala o GitWit no repositório atual ou globalmente para todos os repositórios.

#### Uso:

```bash
gitwit install [-fg] [-hk]
```

| Opção          | Descrição                                                              |
|----------------|------------------------------------------------------------------------|
| `-hk, --hook`  | Instala como hook `prepare-commit-msg` no repositório atual.           |
| `-g, --global` | Instala como alias global do Git, disponível em todos os repositórios. |
| `-f, --force`  | Força a instalação, sobrescrevendo um hook existente.                  |

<br>

::: warning ⚠️ Aviso:
As opções --hook e --global **não podem** ser usadas ao mesmo tempo.
:::

#### Exemplos:

```bash
# Instala no repositório atual como alias do Git
gitwit install

# Instala globalmente como alias do Git
gitwit install --global

# Instala o GitWit como hook no repositório atual
gitwit install --hook
```

## ❌ `uninstall`

Remove a instalação do GitWit do repositório atual ou globalmente.

#### Uso:

```bash
gitwit uninstall [-g] [-hk]
```

| Opção          | Descrição                                                |
|----------------|----------------------------------------------------------|
| `-hk, --hook`  | Remove o hook `prepare-commit-msg` do repositório atual. |
| `-g, --global` | Remove o alias global do GitWit.                         |

<br>

::: warning ⚠️ Aviso:
As opções --hook e --global não podem **ser usadas** juntas.
:::

#### Exemplos:

```bash
# Remove o alias do repositório atual
gitwit uninstall

# Remove a instalação global
gitwit uninstall --global

# Remove o hook do repositório atual
gitwit uninstall --hook
```

## 📝 `commit`

Inicia o assistente interativo de commits ou permite passar dados diretamente por parâmetros.

#### Uso:

```bash
gitwit commit [-aem] [-t=<type>] [-s=<scope>] [-d=<shortDescription>] [-l=<longDescription>] 
```

| Opção                                      | Descrição                                                                |
|--------------------------------------------|--------------------------------------------------------------------------|
| `-a, --add`                                | Adiciona automaticamente todos os arquivos modificados e não rastreados. |
| `-m, --amend`                              | Modifica o último commit em vez de criar um novo.                        |
| `-e, --allow-empty`                        | Permite criar commits vazios.                                            |
| `-t, --type=<type>`                        | Tipo do commit (`feat`, `fix`, `chore` etc.).                            |
| `-s, --scope=<scope>`                      | Escopo do commit (`core`, `ui`, `auth` etc.).                            |
| `-d, --description=<shortDescription>`     | Descrição breve do commit.                                               |
| `-l, --long-description=<longDescription>` | Descrição detalhada do commit.                                           |

#### Exemplos:

```bash
# Assistente interativo
gitwit commit

# Commit direto com parâmetros
gitwit commit -t feat -s core -d "adiciona suporte a logs"

# Commit com arquivos já adicionados e modificação do último commit
gitwit commit --amend --add -t fix -s api -d "corrige autenticação JWT"
```

## 🔍 `lint`

Valida mensagens de commit com base nas regras definidas no `.gitwit`.

O comando pode validar:

- um commit específico
- um intervalo de commits
- ou, por padrão, o commit mais recente (HEAD)

#### Uso:

```bash
gitwit lint [-m=<message>] [<revSpec>]
```

| Opção                     | Descrição                                                                                               |
|---------------------------|---------------------------------------------------------------------------------------------------------|
| `-m, --message=<message>` | Mensagem a ser validada.                                                                                |
| `<revSpec>`               | Especificação de revisão do Git. Pode ser um commit, branch, tag ou um intervalo no formato `from..to`. |

#### Exemplos:

```bash
# Validar apenas o commit mais recente (padrão)
gitwit lint

# Validar um commit específico
gitwit lint 105564ac5c6ca88bee5f3f4978287f5c8f87c07b

# Valida um intervalo de commits
gitwit lint 8d2094..105564a

# Validar uma mensagem sem referência a um commit
gitwit lint -m 'feat(ui): Adicionar tema escuro'
```

## 📜 `changelog`

Gera um changelog estruturado a partir das mensagens de commit do repositório Git, com suporte a tags, intervalos de revisão e incremento automático de versão.

#### Uso:

```bash
gitwit changelog [[-c] [-s=<subtitle>] [-n] [-a] [-S] [-l | --for-tag=<forTag>] [-M | -m | -p]] [<revSpec>]
```

| Opção                | Descrição                                                                                               |
|----------------------|---------------------------------------------------------------------------------------------------------|
| `-c, --copy`         | Copia o changelog gerado para a área de transferência.                                                  |
| `-s, --subtitle`     | Define um subtítulo a ser exibido no changelog.                                                         |
| `-n, --no-subtitle`  | Gera o changelog sem um subtítulo.                                                                      |
| `-a, --append`       | Anexa o changelog ao arquivo existente em vez de sobrescrevê-lo.                                        |
| `-S, --stdout`       | Exibe o changelog gerado no terminal em vez de salvá-lo em um arquivo.                                  |
| `-l, --last-tag`     | Usa a última tag do repositório como ponto inicial para gerar o changelog.                              |
| `--for-tag=<forTag>` | Usa a tag especificada como ponto inicial para gerar o changelog.                                       |
| `-M --major`         | Incrementa a versão **major** a partir da última tag e gera o changelog para a nova versão.             |
| `-m, --minor`        | Incrementa a versão **minor** a partir da última tag e gera o changelog para a nova versão.             |
| `-p, --patch`        | Incrementa a versão **patch** a partir da última tag e gera o changelog para a nova versão.             |
| `<revSpec>`          | Especificação de revisão do Git. Pode ser um commit, branch, tag ou um intervalo no formato `from..to`. |

<br>

::: warning ⚠️ Aviso:
As opções de incremento de versão (-M, -m, -p) são mutuamente exclusivas. Bem como as opções de tag (-l, --for-tag) não podem ser usadas em conjunto.
:::

#### Exemplos:

```bash
# Gera o changelog para a última tag
gitwit changelog -l

# Gera o changelog a partir de uma tag específica
gitwit changelog --for-tag=v1.2.0

# Gera o changelog incrementando a versão minor
gitwit changelog -m

# Gera o changelog para um intervalo de commits
gitwit changelog 8d2094..105564a

# Gera o changelog e copia para a área de transferência
gitwit changelog -l --copy
```
