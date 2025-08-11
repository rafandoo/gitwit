# Getting Started

If this is your first time using GitWit, you've come to the right place.
Here we'll show you how to install and get started quickly.

To better understand what it is and what it's for, see [What is GitWit?](./what-is.md).

## 1. Prerequisites

Before installing GitWit, make sure your environment meets the minimum requirements:

- Java JRE 21 or higher

GitWit is developed in Java, so you need the Java Runtime Environment installed.
To check your Java version, run:

```bash
java -version
```

- Git installed and configured

GitWit interacts directly with Git repositories, so having Git installed is essential. Verify the installation with:

```bash
git --version
```

<br>

::: info 💡 Tip
If you don't have Java or Git installed, check the official pages:

- [Download Java](https://adoptium.net/pt-BR/temurin/releases)
- [Download Git](https://git-scm.com/downloads)
  :::

## 2. Download GitWit

You can download the latest version JAR file directly from the terminal:

```bash
curl -L https://github.com/rafandoo/gitwit/releases/latest/download/gitwit.jar -o gitwit.jar
```

Or, if you prefer, download it manually from the [Releases repository page on GitHub](https://github.com/rafandoo/gitwit/releases/latest).

<br>

::: info ℹ️ Native Installation
Soon, we will also make packages available for simplified installation on Linux and Windows systems.
:::

## 3. Running GitWit

To run GitWit, use the following command:

```bash
java -jar gitwit.jar -h
```

This will display GitWit help, showing the available commands and their options.

To see detailed documentation for each command, go to: [Command Reference](./../reference/commands.md).

## 4. Next Steps

- [GitWit Configuration](./../reference/configuration.md) – learn how to customize your environment.
- [Available Commands](./../reference/commands.md) – detailed technical documentation.
