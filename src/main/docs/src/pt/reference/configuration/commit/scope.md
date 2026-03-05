# Escopo do commit

Define como o escopo do commit deve ser utilizado, permitindo controlar se ele é opcional, obrigatório ou restrito a uma
lista de valores predefinidos.

O escopo representa normalmente a área do projeto afetada pela mudança, como módulos, componentes ou funcionalidades
específicas.

```yaml
scope:
  description: "Especifique o escopo da mudança"
  required: false
  type: list
  values:
    - core
    - cli
    - api
```

| Campo         | Obrigatório | Tipo    | Padrão | Descrição                                                                                                                         |
|---------------|-------------|---------|--------|-----------------------------------------------------------------------------------------------------------------------------------|
| `description` | Não         | String  | -      | Texto exibido como instrução auxiliar no assistente interativo (_wizard_), orientando o usuário sobre o que informar como escopo. |
| `required`    | Não         | Boolean | false  | Define se o usuário deve obrigatoriamente informar um escopo ao criar o commit.                                                   |
| `type`        | Sim         | String  | text   | Define o formato do campo de escopo. Pode ser `text` (entrada livre) ou `list` (seleção entre valores predefinidos).              |
| `values`      | Condicional | List    | -      | Lista de escopos permitidos. Este campo é obrigatório quando `type` estiver configurado como `list`.                              |

<br>

::: warning ⚠️ Aviso:
Quando `required: true` e `type: list`, o usuário deverá obrigatoriamente selecionar um dos escopos definidos para
continuar a criação do commit.
:::
