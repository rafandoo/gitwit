# Install

Instala o GitWit no repositório atual ou configura sua utilização globalmente, permitindo executar os comandos em
qualquer repositório Git.

A instalação pode ser feita como alias do Git ou como um hook automático para integração direta ao fluxo de commits.

## Uso

```bash
gitwit install [-fg] [-hk]
```

| Opção          | Descrição                                                                                                                                             |
|----------------|-------------------------------------------------------------------------------------------------------------------------------------------------------|
| `-hk, --hook`  | Instala o GitWit como hook `prepare-commit-msg` no repositório atual, permitindo integrar automaticamente o GitWit ao processo de criação de commits. |
| `-g, --global` | Instala o GitWit como um alias global do Git, tornando o comando disponível em todos os repositórios do usuário.                                      |
| `-f, --force`  | Força a instalação, sobrescrevendo um hook existente caso já esteja configurado no repositório.                                                       |

<br>

::: warning ⚠️ Aviso:
As opções `--hook` e `--global` **não podem** ser utilizadas ao mesmo tempo.
:::

## Exemplos

```bash
# Instala no repositório atual como alias do Git
gitwit install

# Instala globalmente como alias do Git
gitwit install --global

# Instala o GitWit como hook no repositório atual
gitwit install --hook
```
