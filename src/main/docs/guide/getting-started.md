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

GitWit is available for native installation on all major operating systems.
You can get the packages directly from the [Releases](https://github.com/rafandoo/gitwit/releases) page in the GitHub repository.

In the attachments for each release, you'll find:

- Linux: .deb and .rpm packages (e.g., gitwit_1.0.0-RC2.deb, gitwit_1.0.0-RC2.rpm)
- Windows: .exe installer (e.g., gitwit_1.0.0-RC2.exe)

Choose the package corresponding to your system and follow the standard installation process for your platform.

To check the latest version of GitWit, click [here](https://github.com/rafandoo/gitwit/releases/latest).

## 3. Running GitWit

After installation, GitWit will be available directly from the terminal:

bash
gitwit -h

This command will display GitWit help, listing all available commands and their options.

For detailed documentation for each command, visit [Command Reference](./../reference/commands.md).

## 4. Next Steps

- [GitWit Configuration](./../reference/configuration.md) – learn how to customize your environment.
- [Available Commands](./../reference/commands.md) – detailed technical documentation.
