# Primeiros Passos

Se esta é sua primeira vez utilizando o GitWit, você está no lugar certo.
Aqui vamos mostrar como instalar e começar a usar a ferramenta rapidamente.

Para entender melhor o que é e para que serve, veja [O que é o GitWit?](./what-is.md).

## 1. Pré‑requisitos

Antes de instalar o GitWit, certifique-se de que seu ambiente atende aos requisitos mínimos:

- Git instalado e configurado

  O GitWit interage diretamente com repositórios Git, então é essencial ter o Git instalado.
  Verifique a instalação com:

  ```bash
  git --version
  ```

<br>

::: info 💡 Dica
Se você não tem o Git instalado, consulte a página oficial:

- [Download Git](https://git-scm.com/downloads)
:::

## 2. Download do GitWit

O GitWit está disponível para instalação nativa nos principais sistemas operacionais.
Você pode obter os pacotes diretamente pela página de [Releases](https://github.com/rafandoo/gitwit/releases) no repositório no GitHub.

Nos anexos de cada release você encontrará:

- Linux: pacotes .deb e .rpm (ex.: gitwit_1.0.0.deb, gitwit_1.0.0.rpm)
- Windows: instalador .exe (ex.: gitwit_1.0.0.exe)

Escolha o pacote correspondente ao seu sistema e siga o processo de instalação padrão da sua plataforma.

Para conferir a versão mais recente do GitWit clique [aqui](https://github.com/rafandoo/gitwit/releases/latest).

::: info ⚠ Importante
O GitWit é desenvolvido em Java, portanto requer o Java Runtime Environment (JRE) para funcionar.  
Para simplificar a instalação, todos os pacotes já acompanham uma versão mínima do JRE, não sendo necessário instalá-lo separadamente.  
:::

## 3. Executando o GitWit

Após a instalação, o GitWit estará disponível diretamente pelo terminal:

```bash
gitwit -h
```

Esse comando exibirá a ajuda do GitWit, listando todos os comandos disponíveis e suas opções.

Para consultar a documentação detalhada de cada comando, acesse: [Referência de Comandos](./../reference/commands.md).

## 4. Próximos passos

- [Configuração do GitWit](./../reference/configuration.md) – aprenda a personalizar seu ambiente.
- [Comandos disponíveis](./../reference/commands.md) – documentação técnica detalhada.
