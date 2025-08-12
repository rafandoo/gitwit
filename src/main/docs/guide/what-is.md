# What is GitWit?

<br>
<p align="center">
  <picture>
    <img src="/banner.webp" alt="GitWit Logo" width="50%" style="background-color: rgba(255, 255, 255, 0.85); border-radius: 20px; display: inline-block; box-shadow: 0 2px 8px rgba(0,0,0,0.15);">
  </picture>
</p>

GitWit is a command-line application written in Java, designed to help developers keep their Git repositories organized, coherent, and standardized.
It acts as an intelligent commit assistant, offering an interactive interface for creating commit messages, generating changelogs, and automating conventions such as the [Conventional Commits](https://www.conventionalcommits.org/) standard.

## 🧠 What does the application do?

GitWit intercepts or assists Git commands related to commits and changelogs and performs actions based on project
configuration. It:

- Provides an interactive wizard for creating commits (with support for types, scopes, and detailed messages).
- Automatically validates commit messages according to defined rules (e.g., prefixes like `feat`, `fix`, `docs`, etc.).

- Generates changelogs based on the repository's history, organizing entries by type and scope.
- Allows for customized configuration of rules, scopes, types, and more.

## 🧑‍💻 Who is GitWit for?

GitWit is ideal for:

- Developers who want to standardize commits and changelogs in personal or team projects.
- Teams that follow guidelines such as Conventional Commits and SemVer, or integrate with pipelines that depend on them.
- Open-source projects that want to facilitate external contributions while maintaining consistency.

## 🎯 Why use GitWit?

- ✅ Prevents human error in commit messages.
- ✅ Ensures a clean and semantic version history.
- ✅ Increases repository readability and maintainability.
- ✅ Automates release-ready changelogs.
- ✅ Adapts to your project — you define the types, scopes, and validations.
