# Comando principal

O comando principal do GitWit permite acessar as funcionalidades da ferramenta e executar seus subcomandos.
Também disponibiliza opções globais, como exibição de ajuda, versão instalada, geração de configuração de exemplo e modo
de depuração.

## Uso

```bash
gitwit [-dhV] [-ce] [COMMAND]
```

| Opção                   | Descrição                                                                                                 |
|-------------------------|-----------------------------------------------------------------------------------------------------------|
| `-d, --debug`           | Ativa o modo de depuração, exibindo informações adicionais durante a execução do comando.                 |
| `-ce, --config-example` | Gera um arquivo `.gitwit` de exemplo no diretório atual, contendo uma configuração inicial da ferramenta. |
| `-h, --help`            | Exibe a ajuda geral do GitWit, listando os comandos disponíveis e suas opções.                            |
| `-V, --version`         | Exibe a versão atualmente instalada do GitWit.                                                            |
