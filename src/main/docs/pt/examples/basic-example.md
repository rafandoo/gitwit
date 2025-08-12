# Exemplo básico de configuração

Abaixo está um exemplo básico de configuração do Gitwit, que pode ser usado como ponto de partida para personalizar seu fluxo de trabalho:

```yaml
types:
  description: "Selecione o tipo de alteração que você está realizando"
  values:
    feat: "Uma nova funcionalidade"
    fix: "Correção de um bug"
    docs: "Mudanças na documentação"
    refactor: "Alterações de código que não corrigem um bug nem adicionam um recurso"
    test: "Adicionar ou corrigir testes"
    chore: "Outras alterações que não modificam os arquivos src ou test"
    build: "Alterações que afetam o sistema de build ou dependências externas"
    bump: "Atualizações de dependências"
    down: "Downgrades de dependências"
    remove: "Remoção de código ou arquivos"
    sec: "Alterações relacionadas à segurança"

scope:
  description: "Especifique o escopo da mudança (e.g. component, module, etc.)"
  required: true
  type: list
  values:
    - core
    - api
    - cli
    - config

shortDescription:
  description: "Forneça um breve resumo descritivo da mudança"
  required: true
  minLength: 15
  maxLength: 70

longDescription:
  enabled: true
  description: "Detalhes adicionais sobre a mudança, por que ela foi feita e qualquer contexto"
  required: true
  minLength: 20
  maxLength: 100

changelog:
  title: "Changelog"
  types:
    feat: "Novas funcionalidades"
    fix: "Correções de bugs"
    refactor: "Refatorações de código"
    docs: "Atualizações de documentação"
    test: "Testes adicionados ou corrigidos"
    sec: "Correções de segurança"
  showOtherTypes: true
  ignored:
    - chore
  format:
    sectionTemplate: "{scope}: {description} ({shortHash})"
    breakingChangesTemplate: "{type} ({scope})!: {description} ({shortHash})"
    otherTypesTemplate: "{type} ({scope}): {description} ({shortHash})"
    defaultTemplate: "{type}: {description}"
```
