# Uninstall

Remove o GitWit do repositório atual ou desfaz sua instalação global, restaurando o comportamento padrão do Git sem a
integração da ferramenta.

A remoção pode afetar tanto aliases configurados quanto hooks instalados previamente.

## Uso

```bash
gitwit uninstall [-g] [-hk]
```

| Opção          | Descrição                                                                                |
|----------------|------------------------------------------------------------------------------------------|
| `-hk, --hook`  | Remove o hook `prepare-commit-msg` configurado no repositório atual.                     |
| `-g, --global` | Remove o alias global do GitWit, deixando o comando indisponível em outros repositórios. |

<br>

::: warning ⚠️ Aviso:
As opções `--hook` e `--global` não podem **ser usadas** juntas.
:::

## Exemplos

```bash
# Remove o alias configurado no repositório atual
gitwit uninstall

# Remove a instalação global do GitWit
gitwit uninstall --global

# Remove o hook do repositório atual
gitwit uninstall --hook
```
