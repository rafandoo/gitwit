# Lint

Valida mensagens de commit com base nas regras configuradas no arquivo `.gitwit`, garantindo que os commits sigam o
padrão definido pelo projeto.

O comando pode ser utilizado para validar commits já existentes no repositório ou uma mensagem informada manualmente,
sendo útil tanto para uso local quanto em automações e pipelines.

O `lint` pode validar:

- um commit específico;
- um intervalo de commits;
- ou, por padrão, o commit mais recente (HEAD).

## Uso

```bash
gitwit lint [-m=<message>] [<revSpec>]
```

| Opção                     | Descrição                                                                                                                                                              |
|---------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `-m, --message=<message>` | Valida uma mensagem de commit informada diretamente, sem necessidade de referenciar um commit do repositório.                                                          |
| `<revSpec>`               | Especificação de revisão do Git utilizada para selecionar os commits a serem validados. Pode ser um hash de commit, branch, tag ou um intervalo no formato `from..to`. |

## Exemplos

```bash
# Valida apenas o commit mais recente (comportamento padrão)
gitwit lint

# Valida um commit específico pelo hash
gitwit lint 105564ac5c6ca88bee5f3f4978287f5c8f87c07b

# Valida um intervalo de commits
gitwit lint 8d2094..105564a

# Valida uma mensagem sem referência a um commit
gitwit lint -m 'feat(ui): Adicionar tema escuro'
```
