# Geração de changelog

Esta seção define as configurações responsáveis pela geração automática do changelog do projeto a partir do histórico de
commits.
Por meio dessas opções, é possível controlar quais commits serão incluídos, como eles serão agrupados em seções e de que
forma serão exibidos no arquivo final.

O changelog é construído com base nos tipos de commit configurados, permitindo organizar alterações como novas
funcionalidades, correções e mudanças incompatíveis de maneira padronizada e legível para os usuários do projeto.

Exemplo de configuração para changelog:

```yaml
changelog:
  title: "Changelog"
  filepath: "CHANGELOG.md"
  types:
    feat: "Novas funcionalidades"
    fix: "Correções"
  showOtherTypes: true
  showBreakingChanges: true
  ignored:
    - chore
```

| Campo                 | Obrigatório | Tipo    | Padrão                                                 | Descrição                                                                                                                                                                                                            |
|-----------------------|-------------|---------|--------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `title`               | Não         | String  | "Changelog"                                            | Título usado no topo do changelog gerado.                                                                                                                                                                            |
| `filepath`            | Não         | String  | "CHANGELOG.md"                                         | Caminho do arquivo onde o changelog será criado ou atualizado. Pode ser relativo à raiz do projeto.                                                                                                                  |
| `types`               | Sim         | Map     | -                                                      | Define quais tipos de commit aparecerão como seções do changelog e o nome exibido para cada seção.                                                                                                                   |
| `showOtherTypes`      | Não         | Boolean | true                                                   | Quando habilitado, inclui commits cujo tipo não está definido em types, agrupando-os em uma seção separada.                                                                                                          |
| `showBreakingChanges` | Não         | Boolean | false                                                  | Adiciona uma seção específica para commits marcados como _breaking changes_.                                                                                                                                         |
| `ignored`             | Não         | List    | `Merge`, `Revert`, `Pull request`, `fixup!`, `squash!` | Lista de padrões utilizados para filtrar commits durante a geração do changelog. Caso a mensagem completa do commit contenha qualquer um dos valores definidos, o commit será ignorado e não aparecerá no changelog. |

## Templates de exibição

Esta seção define como os commits serão formatados e apresentados no changelog gerado.
Por meio dos templates, é possível personalizar a estrutura textual de cada entrada, controlando quais informações do
commit serão exibidas e em qual formato.

Cada template é aplicado conforme o tipo de commit e o contexto da geração (como commits comuns, breaking changes ou
tipos não configurados), permitindo adaptar o changelog ao padrão de documentação do projeto.

Exemplo de configuração para templates de exibição:

```yaml
changelog:
  # outras configurações...

  format:
    sectionTemplate: "{scope}: {description} ({shortHash})"
    breakingChangesTemplate: "{type} ({scope})!: {description} ({shortHash})"
    otherTypesTemplate: "{type} ({scope}): {description} ({shortHash})"
    defaultTemplate: "{type}: {description}"
```

| Campo                     | Obrigatório | Tipo   | Descrição                                                                                                     |
|---------------------------|-------------|--------|---------------------------------------------------------------------------------------------------------------|
| `sectionTemplate`         | Não         | String | Template utilizado para commits cujos tipos estão definidos em `types`.                                       |
| `breakingChangesTemplate` | Não         | String | Template aplicado exclusivamente a commits marcados como _breaking changes_.                                  |
| `otherTypesTemplate`      | Não         | String | Template usado para commits de tipos não configurados em `types`, quando `showOtherTypes` estiver habilitado. |
| `defaultTemplate`         | Não         | String | Template de fallback utilizado caso nenhum outro template seja aplicável.                                     |

### Variáveis disponíveis nos templates

As seguintes variáveis podem ser utilizadas em qualquer template:

- `{type}` - commit type (e.g. `feat`, `fix`)
- `{scope}` - scope informed at commit
- `{description}` - short description of the commit
- `{hash}` - complete commit hash
- `{shortHash}` - short hash version (first 7 characters)
- `{breakingChanges}` - breaking change indicator
- `{author}` - commit author
- `{date}` - commit date and time
