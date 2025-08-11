# Primeiros Passos

Se esta é sua primeira vez utilizando o GitWit, você está no lugar certo.
Aqui vamos mostrar como instalar e começar a usar a ferramenta rapidamente.

Para entender melhor o que é e para que serve, veja [O que é o GitWit?](./what-is.md).

## 1. Pré‑requisitos

Antes de instalar o GitWit, certifique-se de que seu ambiente atende aos requisitos mínimos:

- Java JRE 21 ou superior

  O GitWit é desenvolvido em Java, portanto você precisa do Java Runtime Environment instalado.
  Para verificar sua versão do Java, execute:

  ```bash
  java -version
  ```

- Git instalado e configurado

  O GitWit interage diretamente com repositórios Git, então é essencial ter o Git instalado.
  Verifique a instalação com:

  ```bash
  git --version
  ```

<br>

::: info 💡 Dica
Se você não tem o Java ou o Git instalados, consulte as páginas oficiais:

- [Download Java](https://adoptium.net/pt-BR/temurin/releases)
- [Download Git](https://git-scm.com/downloads)
  :::

## 2. Download do GitWit

Você pode baixar o arquivo JAR da versão mais recente diretamente pelo terminal:

```bash
curl -L https://github.com/rafandoo/gitwit/releases/latest/download/gitwit.jar -o gitwit.jar
```

Ou, se preferir, baixe manualmente pela página de [Releases repositório no GitHub](https://github.com/rafandoo/gitwit/releases/latest).

<br>

::: info ℹ️ Instalação nativa
Em breve, também disponibilizaremos pacotes para instalação simplificada nos sistemas Linux e Windows.
:::

## 3. Executando o GitWit

Para executar o GitWit, use o seguinte comando:

```bash
java -jar gitwit.jar -h
```

Isso exibirá a ajuda do GitWit, mostrando os comandos disponíveis e suas opções.

Para ver a documentação detalhada de cada comando, acesse: [Referência de Comandos](./../reference/commands.md).

## 4. Próximos passos

- [Configuração do GitWit](./../reference/configuration.md) – aprenda a personalizar seu ambiente.
- [Comandos disponíveis](./../reference/commands.md) – documentação técnica detalhada.
