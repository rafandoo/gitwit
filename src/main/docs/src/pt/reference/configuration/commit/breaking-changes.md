# Mudanças críticas

Configura um campo opcional para registrar mudanças que quebram compatibilidade (breaking changes), ou seja, alterações
que podem exigir adaptações por parte dos usuários ou outros sistemas que dependem do projeto.

Quando habilitado, o usuário poderá informar essas mudanças durante a criação do commit, permitindo destacá-las
posteriormente em ferramentas como o changelog.

```yaml
breakingChanges:
  enabled: true
  description: "Liste as mudanças significativas"
```

| Campo         | Obrigatório | Tipo    | Padrão | Descrição                                                                                                                         |
|---------------|-------------|---------|--------|-----------------------------------------------------------------------------------------------------------------------------------|
| `enabled`     | Não         | Boolean | false  | Ativa ou desativa o campo para registro de breaking changes durante a criação do commit.                                          |
| `description` | Não         | String  | -      | Texto exibido como instrução auxiliar no assistente interativo (_wizard_), orientando o usuário a descrever as mudanças críticas. |
