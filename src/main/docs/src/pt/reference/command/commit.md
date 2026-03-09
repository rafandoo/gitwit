# Commit

Inicia o assistente interativo para criação de commits padronizados ou permite informar os dados do commit diretamente
por meio de parâmetros na linha de comando.

O comando guia o usuário na construção da mensagem de commit conforme as regras configuradas no projeto, garantindo
consistência e validação automática.

## Uso

```bash
gitwit commit [-aem] [-t=<type>] [-s=<scope>] [-d=<shortDescription>] [-l=<longDescription>] 
```

| Opção                                      | Descrição                                                                                           |
|--------------------------------------------|-----------------------------------------------------------------------------------------------------|
| `-a, --add`                                | Adiciona automaticamente todos os arquivos modificados e não rastreados antes de criar o commit.    |
| `-m, --amend`                              | Altera o último commit existente em vez de criar um novo commit.                                    |
| `-e, --allow-empty`                        | Permite a criação de commits sem alterações nos arquivos.                                           |
| `-t, --type=<type>`                        | Define o tipo do commit (ex.: `feat`, `fix`, `chore`), conforme os tipos configurados no projeto.   |
| `-s, --scope=<scope>`                      | Define o escopo da alteração, indicando a área ou módulo afetado (ex.: `core`, `ui`, `auth`).       |
| `-d, --description=<shortDescription>`     | Define a descrição curta do commit, utilizada como resumo principal da mudança.                     |
| `-l, --long-description=<longDescription>` | Define a descrição longa do commit, utilizada para adicionar detalhes adicionais sobre a alteração. |

## Exemplos

```bash
# Inicia o assistente interativo
gitwit commit

# Cria um commit informando os dados diretamente pelos parâmetros
gitwit commit -t feat -s core -d "adiciona suporte a logs"

# Atualiza o último commit e adiciona automaticamente os arquivos modificados
gitwit commit --amend --add -t fix -s api -d "corrige autenticação JWT"
```
