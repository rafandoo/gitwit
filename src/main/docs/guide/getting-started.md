# Getting Started

If this is your first time using GitWit, you've come to the right place.
Here we'll show you how to install and get started quickly.

To better understand what it is and what it's for, see [What is GitWit?](./what-is.md).

## 1. Prerequisites

Before installing GitWit, make sure your environment meets the minimum requirements:

- Git installed and configured

  GitWit interacts directly with Git repositories, so having Git installed is essential. 
  Verify the installation with:
  
  ```bash
  git --version
  ```

<br>

::: info 💡 Tip
If you don't have Git installed, check the official page:

- [Download Git](https://git-scm.com/downloads)
:::

## 2. Download GitWit

GitWit is available for native installation on all major operating systems.
You can get the packages directly from the [Releases](https://github.com/rafandoo/gitwit/releases) page in the GitHub repository.

In the attachments for each release, you'll find:

- Linux: .deb and .rpm packages (e.g., gitwit_1.0.0.deb, gitwit_1.0.0.rpm)
- Windows: .exe installer (e.g., gitwit_1.0.0.exe)

Choose the package corresponding to your system and follow the standard installation process for your platform.

To check the latest version of GitWit, click [here](https://github.com/rafandoo/gitwit/releases/latest).

::: info ⚠ Important
GitWit is developed in Java, so it requires the Java Runtime Environment (JRE) to function.
To simplify installation, all packages already include a minimum version of the JRE, so you don't need to install it separately.
:::

## 3. Running GitWit

After installation, GitWit will be available directly from the terminal:

```bash
gitwit -h
```

This command will display GitWit help, listing all available commands and their options.

For detailed documentation for each command, visit [Command Reference](./../reference/commands.md).

## 4. Next Steps

- [GitWit Configuration](./../reference/configuration.md) – learn how to customize your environment.
- [Available Commands](./../reference/commands.md) – detailed technical documentation.
