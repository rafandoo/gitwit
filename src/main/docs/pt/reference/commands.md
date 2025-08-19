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
gitwit install [-fghV] [-hk]
```

| Opção           | Descrição                                                              |
|-----------------|------------------------------------------------------------------------|
| `-hk, --hook`   | Instala como hook `prepare-commit-msg` no repositório atual.           |
| `-g, --global`  | Instala como alias global do Git, disponível em todos os repositórios. |
| `-f, --force`   | Força a instalação, sobrescrevendo um hook existente.                  |
| `-h, --help`    | Mostra a ajuda do comando.                                             |
| `-V, --version` | Mostra a versão.                                                       |

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
gitwit uninstall [-ghV] [-hk]
```

| Opção           | Descrição                                                |
|-----------------|----------------------------------------------------------|
| `-hk, --hook`   | Remove o hook `prepare-commit-msg` do repositório atual. |
| `-g, --global`  | Remove o alias global do GitWit.                         |
| `-h, --help`    | Mostra a ajuda do comando.                               |
| `-V, --version` | Mostra a versão.                                         |

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
gitwit commit [-ahV] [-am] [-d=<shortDescription>] [-l=<longDescription>] [-s=<scope>] [-t=<type>]
```

| Opção                                      | Descrição                                                                |
|--------------------------------------------|--------------------------------------------------------------------------|
| `-a, --add`                                | Adiciona automaticamente todos os arquivos modificados e não rastreados. |
| `-am, --amend`                             | Modifica o último commit em vez de criar um novo.                        |
| `-t, --type=<type>`                        | Tipo do commit (`feat`, `fix`, `chore` etc.).                            |
| `-s, --scope=<scope>`                      | Escopo do commit (`core`, `ui`, `auth` etc.).                            |
| `-d, --description=<shortDescription>`     | Descrição breve do commit.                                               |
| `-l, --long-description=<longDescription>` | Descrição detalhada do commit.                                           |
| `-h, --help`                               | Mostra a ajuda.                                                          |
| `-V, --version`                            | Mostra a versão.                                                         |

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

#### Uso:

```bash
gitwit lint [-hV] [-f=<from>] [-t=<to>]
```

| Opção               | Descrição                                   |
|---------------------|---------------------------------------------|
| `-f, --from=<from>` | Commit inicial (inclusivo). Padrão: `HEAD`. |
| `-t, --to=<to>`     | Commit final (inclusivo).                   |
| `-h, --help`        | Mostra a ajuda.                             |
| `-V, --version`     | Mostra a versão.                            |

#### Exemplos:

```bash
# Lint no último commit
gitwit lint

# Lint de um intervalo de commits
gitwit lint --from v1.0.0 --to v1.1.0
```

## 📜 `changelog`

Gera um changelog formatado a partir das mensagens de commit.

#### Uso:

```bash
gitwit changelog [-achV] -f=<from> [-t=<to>] [-s=<subtitle>] 
```

| Opção               | Descrição                                                               |
|---------------------|-------------------------------------------------------------------------|
| `-f, --from=<from>` | Ponto inicial (hash, tag ou branch).                                    |
| `-t, --to=<to>`     | Ponto final. Padrão: `HEAD`.                                            |
| `-s, --subtitle`    | Subtítulo a ser exibido no changelog.                                   |
| `-a, --append`      | Indica se o changelog será anexado ao arquivo existente ou sobrescrito. |                 
| `-c, --copy`        | Copia o changelog gerado para a área de transferência.                  |
| `-h, --help`        | Mostra a ajuda.                                                         |
| `-V, --version`     | Mostra a versão.                                                        |

#### Exemplos:

```bash
# Gera changelog desde a última tag
gitwit changelog --from v1.0.0

# Gera changelog e copia para a área de transferência
gitwit changelog --from v1.0.0 --to v1.2.0 --copy
```
