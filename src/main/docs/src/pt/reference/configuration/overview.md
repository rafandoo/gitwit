# Configuração do GitWit

O GitWit utiliza um arquivo de configuração chamado `.gitwit`, localizado no diretório
**raiz do projeto** — o mesmo onde está o diretório `.git`.

Esse arquivo define:

- Regras de validação para commits.
- Campos obrigatórios ou opcionais.
- Escopos e tipos permitidos.
- Formatação para commits e changelogs.

Esta página documenta todas as chaves disponíveis e como usá-las, com exemplos e valores padrão.

## Estrutura básica do arquivo

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

## Exemplos de configuração

A seguir, você encontrará dois modelos prontos para uso, cada um com um estilo próprio:

- [Exemplo Básico](./examples/basic-example.md) — ideal para quem está começando ou deseja manter o padrão de commits
  simples e objetivo.
- [Exemplo com Emojis](./examples/emoji-example.md) — perfeito para quem quer adicionar expressividade e facilitar a
  identificação visual dos commits no histórico.

Use o que melhor se adapta ao seu fluxo de trabalho — ou combine ideias para criar a sua própria configuração
personalizada.
