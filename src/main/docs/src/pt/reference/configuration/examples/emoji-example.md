# Exemplo de configuração com emojis

Além do formato tradicional do Conventional Commits, há uma abordagem popular que utiliza emojis para classificar e dar
mais expressividade às mensagens de commit.

Essa prática facilita a leitura e torna o histórico mais visual, permitindo identificar rapidamente o tipo de alteração
feita — mesmo sem ler toda a descrição.

Uma das convenções mais conhecidas é o [Gitmoji](https://gitmoji.dev/), que mantém uma lista padronizada de emojis e
seus respectivos significados.

O GitWit é totalmente compatível com esse estilo e permite configurá-lo facilmente.

Abaixo está um exemplo de arquivo de configuração YAML para usar o GitWit com emojis:

```yaml
types:
  description: "Selecione o tipo de alteração que você está realizando"
  values:
    ✨: "Uma nova funcionalidade"
    🐛: "Correção de um bug"
    📝: "Apenas alterações na documentação"
    ♻️: "Mudanças de código que não corrigem um bug nem adicionam um recurso"
    ✅: "Adicionando ou corrigindo testes"
    🧹: "Outras mudanças que não modificam src ou arquivos de teste"
    📦: "Mudanças que afetam o sistema de compilação ou dependências externas"
    ⬆️: "Atualizações de dependência"
    ⬇️: "Desclassificação de dependência"
    🔥: "Removendo código ou arquivos"
    🔒: "Mudanças relacionadas à segurança"
    🌐: "Internacionalização ou localização"

scope:
  description: "Especifique o escopo da mudança (e.g. component, module, etc.)"
  required: false
  type: text

shortDescription:
  description: "Forneça um breve resumo descritivo da mudança"
  required: true
  minLength: 5
  maxLength: 70

changelog:
  title: "Changelog"
  types:
    ✨: "Novas funcionalidades"
    🐛: "Correções de bugs"
    ♻️: "Refatorações de código"
    📝: "Atualizações na documentação"
    ✅: "Testes adicionados ou corrigidos"
    🔒: "Correções de segurança"
  showOtherTypes: true
  ignored:
    - 🧹
  format:
    sectionTemplate: "{scope}: {description} ({shortHash})"
    breakingChangesTemplate: "{type} ({scope})!: {description} ({shortHash})"
    otherTypesTemplate: "{type} ({scope}): {description} ({shortHash})"
    defaultTemplate: "{type}: {description}"
```
