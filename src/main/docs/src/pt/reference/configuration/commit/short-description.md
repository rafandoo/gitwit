# Descrição curta do commit

Define as regras de validação para a descrição curta do commit, que representa o resumo principal da alteração
realizada.

Esse campo é obrigatório e corresponde à mensagem resumida exibida no histórico de commits, sendo utilizado para
identificar rapidamente o propósito da mudança.

```yaml
shortDescription:
  description: "Forneça um breve resumo"
  minLength: 5
  maxLength: 70
```

| Campo         | Obrigatório | Tipo   | Padrão | Descrição                                                                                                                               |
|---------------|-------------|--------|--------|-----------------------------------------------------------------------------------------------------------------------------------------|
| `description` | Não         | String | -      | Texto exibido como instrução auxiliar no assistente interativo (_wizard_), orientando o usuário sobre como preencher a descrição curta. |
| `minLength`   | Não         | Int    | 1      | Define a quantidade mínima de caracteres permitida para a descrição curta do commit.                                                    |
| `maxLength`   | Não         | Int    | 72     | Define o limite máximo de caracteres permitido para a descrição curta, ajudando a manter as mensagens concisas e legíveis no histórico. |
