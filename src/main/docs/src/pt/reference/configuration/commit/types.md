# Tipos de commit

Define quais tipos de commit podem ser utilizados e a descrição associada a cada um deles.
Esses tipos são usados durante a criação e validação dos commits, ajudando a padronizar as mensagens e organizar melhor
o histórico do projeto.

```yaml
types:
  description: "Selecione o tipo de alteração que você está realizando"
  values:
    feat: "Uma nova funcionalidade"
    fix: "Correção de um bug"
    docs: "Mudanças na documentação"
```

| Campo         | Obrigatório | Tipo   | Descrição                                                                                                                                                                |
|---------------|-------------|--------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `description` | Não         | String | Texto exibido como instrução ou mensagem auxiliar no assistente interativo (_wizard_), orientando o usuário na escolha do tipo de commit.                                |
| `values`      | Sim         | Map    | Lista dos tipos de commit permitidos, onde a chave representa o identificador do tipo (ex.: `feat`, `fix`) e o valor é a descrição exibida ao usuário durante a seleção. |

<br>

::: tip 💡 Boas práticas:

- Utilize nomes curtos e objetivos (preferencialmente até 10 caracteres).
- Mantenha os tipos consistentes entre todos os colaboradores do projeto.
- Prefira termos amplamente reconhecidos, como `feat`, `fix`, `docs`, `test` e `refactor`, para facilitar a leitura do
  histórico de commits.
:::
