# Configuração do GitWit

O GitWit utiliza um arquivo de configuração chamado `.gitwit`, localizado no diretório
**raiz do projeto** — o mesmo onde está o diretório `.git`.

Esse arquivo define:

- Regras de validação para commits.
- Campos obrigatórios ou opcionais.
- Escopos e tipos permitidos.
- Formatação para commits e changelogs.

Esta página documenta todas as chaves disponíveis e como usá-las, com exemplos e valores padrão.

## 1. Estrutura básica do arquivo

A configuração é organizada em seções, cada uma com um propósito específico.
O arquivo de configuração utiliza o formato YAML para definição das propriedades

::: details 📃 Gerar exemplo de configuração
Você pode gerar automaticamente um arquivo de exemplo executando:

```bash
gitwit --config-example
```

Esse comando criará no diretório atual um `.gitwit` pré-preenchido com valores padrão,
pronto para ser ajustado conforme as suas necessidades.
:::

Exemplo básico:

```yaml
types:
  description: "Selecione o tipo de alteração"
  values:
    feat: "Nova funcionalidade"
    fix: "Correção de bug"

scope:
  type: list
  values:
    - core
    - api
    - cli

shortDescription:
  minLength: 5
  maxLength: 70

changelog:
  title: "Histórico de mudanças"
  types:
    feat: "Funcionalidades"
    fix: "Correções"
  showBreakingChanges: true
```

## 2. `types` - Tipos de commit

Define **quais tipos de commit são permitidos** e uma descrição para cada um.

```yaml
types:
  description: "Selecione o tipo de alteração que você está realizando"
  values:
    feat: "Uma nova funcionalidade"
    fix: "Correção de um bug"
    docs: "Mudanças na documentação"
```

| Campo         | Obrigatório | Tipo   | Descrição                                                |
|---------------|-------------|--------|----------------------------------------------------------|
| `description` | Não         | String | Texto auxiliar para exibição no wizard                   |
| `values`      | Sim         | Map    | Tipos de commit permitidos e suas respectivas descrições |

<br>

::: tip 💡 Boas práticas:

Use nomes curtos (máx. 10 caracteres) para os tipos.

Prefira termos reconhecidos, como feat, fix, docs, test, refactor.
:::

## 2. `scope` - Escopo do commit

Controla o uso do escopo do commit: se é obrigatório, livre ou limitado a uma lista.

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

| Campo         | Obrigatório | Tipo    | Padrão | Descrição                                                             |
|---------------|-------------|---------|--------|-----------------------------------------------------------------------|
| `description` | Não         | String  | -      | Texto auxiliar para exibição no wizard                                |
| `required`    | Não         | Boolean | false  | Define se o escopo é obrigatório                                      |
| `type`        | Sim         | String  | text   | Pode ser `text` (campo livre) ou `list` (escolha entre valores fixos) |
| `values`      | Condicional | List    | -      | Lista de escopos válidos (obrigatório quando `type: list`)            |

<br>

::: warning ⚠️ Aviso:
Se `required: true` e `type: list`, o usuário será obrigado a escolher um dos escopos definidos.
:::

## 3. `shortDescription` - Descrição curta do commit

Define regras para o campo obrigatório de descrição curta do commit.

```yaml
shortDescription:
  description: "Forneça um breve resumo"
  minLength: 5
  maxLength: 70
```

| Campo         | Obrigatório | Tipo   | Padrão | Descrição                              |
|---------------|-------------|--------|--------|----------------------------------------|
| `description` | Não         | String | -      | Texto auxiliar para exibição no wizard |
| `minLength`   | Não         | Int    | 1      | Mínimo de caracteres permitidos        |
| `maxLength`   | Não         | Int    | 72     | Máximo de caracteres permitidos        |

## 4. `longDescription` - Descrição longa do commit

Habilita um campo adicional para detalhamento do commit.

```yaml
longDescription:
  enabled: true
  description: "Detalhes adicionais"
  required: false
  minLength: 20
  maxLength: 100
```

| Campo         | Obrigatório | Tipo    | Padrão | Descrição                              |
|---------------|-------------|---------|--------|----------------------------------------|
| `enabled`     | Não         | Boolean | false  | Ativa o campo de descrição longa       |
| `description` | Não         | String  | -      | Texto auxiliar para exibição no wizard |
| `required`    | Não         | Boolean | false  | Define se é obrigatório                |
| `minLength`   | Não         | Int     | 0      | Mínimo de caracteres permitidos        |
| `maxLength`   | Não         | Int     | 100    | Máximo de caracteres permitidos        |

## 5. `breakingChanges` - Mudanças críticas

Permite destacar mudanças que quebram compatibilidade.

```yaml
breakingChanges:
  enabled: true
  description: "Liste as mudanças significativas"
```

| Campo         | Obrigatório | Tipo    | Padrão | Descrição                              |
|---------------|-------------|---------|--------|----------------------------------------|
| `enabled`     | Não         | Boolean | false  | Ativa o campo para breaking changes    |
| `description` | Não         | String  | -      | Texto auxiliar para exibição no wizard |

## 6. `changelog` - Geração de changelog

Configura como o changelog será montado.

```yaml
changelog:
  title: "Changelog"
  types:
    feat: "Novas funcionalidades"
    fix: "Correções"
  showOtherTypes: true
  showBreakingChanges: true
  ignored:
    - chore
```

| Campo                 | Obrigatório | Tipo    | Padrão      | Descrição                                         |
|-----------------------|-------------|---------|-------------|---------------------------------------------------|
| `title`               | Não         | String  | "Changelog" | Título usado no topo do changelog gerado          |
| `types`               | Sim         | Map     | -           | Mapeia tipos de commit para seções do changelog   |
| `showOtherTypes`      | Não         | Boolean | true        | Mostra tipos não listados em `types`              |
| `showBreakingChanges` | Não         | Boolean | false       | Inclui seção de breaking changes                  |
| `ignored`             | Não         | List    | -           | Tipos de commit ignorados na geração do changelog |

## 7. `changelog.format` - Templates de exibição

Controla o template de exibição dos commits no changelog.

```yaml
format:
  sectionTemplate: "{scope}: {description} ({shortHash})"
  breakingChangesTemplate: "{type} ({scope})!: {description} ({shortHash})"
  otherTypesTemplate: "{type} ({scope}): {description} ({shortHash})"
  defaultTemplate: "{type}: {description}"
```

| Campo                     | Obrigatório | Tipo   | Descrição                                 |
|---------------------------|-------------|--------|-------------------------------------------|
| `sectionTemplate`         | Não         | String | Template para tipos listados em `types`   |
| `breakingChangesTemplate` | Não         | String | Template específico para breaking changes |
| `otherTypesTemplate`      | Não         | String | Template para tipos não listados          |
| `defaultTemplate`         | Não         | String | Template genérico como fallback           |

Variáveis suportadas nos templates:

- `{type}`: tipo do commit (feat, fix, etc.)
- `{scope}`: escopo definido
- `{description}`: descrição curta
- `{hash}`: hash completo do commit
- `{shortHash}`: primeiros 7 caracteres do hash
- `{breakingChanges}`: tag de breaking change
- `{author}`: autor do commit
- `{date}`: data e hora do commit

## 8. `lint` - Regras de linting

Define regras adicionais para validação dos commits.

```yaml
lint:
  ignored:
    - Merge
    - Pull request
```

| Campo     | Obrigatório | Tipo | Padrão                                                 | Descrição                                       |
|-----------|-------------|------|--------------------------------------------------------|-------------------------------------------------|
| `ignored` | Não         | List | "Merge", "Revert", "Pull request", "fixup!", "squash!" | Lista de padrões de mensagens a serem ignorados |

## Exemplos de configuração

A seguir, você encontrará dois modelos prontos para uso, cada um com um estilo próprio:

- [Exemplo Básico](./../examples/basic-example.md) — ideal para quem está começando ou deseja manter o padrão de commits simples e objetivo.
- [Exemplo com Emojis](./../examples/emoji-example.md) — perfeito para quem quer adicionar expressividade e facilitar a identificação visual dos commits no histórico.

Use o que melhor se adapta ao seu fluxo de trabalho — ou combine ideias para criar sua própria configuração personalizada.
