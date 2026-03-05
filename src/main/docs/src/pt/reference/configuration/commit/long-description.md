# Descrição longa do commit

Configura um campo opcional para adicionar uma descrição detalhada ao commit, permitindo complementar o resumo principal
com mais contexto sobre a alteração realizada.

Esse campo pode ser utilizado para explicar motivações, decisões técnicas ou informações adicionais relevantes que não
cabem na descrição curta.

```yaml
longDescription:
  enabled: true
  description: "Detalhes adicionais"
  required: false
  minLength: 20
  maxLength: 100
```

| Campo         | Obrigatório | Tipo    | Padrão | Descrição                                                                                                                 |
|---------------|-------------|---------|--------|---------------------------------------------------------------------------------------------------------------------------|
| `enabled`     | Não         | Boolean | false  | Ativa ou desativa o campo de descrição longa durante a criação do commit.                                                 |
| `description` | Não         | String  | -      | Texto exibido como instrução auxiliar no assistente interativo (_wizard_), orientando o preenchimento da descrição longa. |
| `required`    | Não         | Boolean | false  | Define se o preenchimento da descrição longa será obrigatório quando o campo estiver habilitado.                          |
| `minLength`   | Não         | Int     | 0      | Quantidade mínima de caracteres permitida para a descrição longa.                                                         |
| `maxLength`   | Não         | Int     | 100    | Quantidade máxima de caracteres permitida para a descrição longa.                                                         |
