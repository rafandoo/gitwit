# Regras de linting

Define regras adicionais utilizadas durante a validação das mensagens de commit.
Essas configurações permitem ignorar automaticamente commits específicos no processo de linting, evitando que commits
gerados automaticamente ou auxiliares sejam validados.

Exemplo de configuração:

```yaml
lint:
  ignored:
    - Merge
    - Pull request
```

| Campo     | Obrigatório | Tipo | Padrão                                                 | Descrição                                                                                                                                                                                                 |
|-----------|-------------|------|--------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `ignored` | Não         | List | `Merge`, `Revert`, `Pull request`, `fixup!`, `squash!` | Lista de padrões aplicados às mensagens de commit durante o linting. Caso a mensagem do commit contenha qualquer um dos valores definidos, o commit será ignorado e não passará pelas validações de lint. |
