<p align="center">
  <picture>
    <img src="./src/main/docs/public/banner.webp" alt="GitWit Logo" width="50%" style="background-color: rgba(255, 255, 255, 0.85); border-radius: 20px; display: inline-block; box-shadow: 0 2px 8px rgba(0,0,0,0.15);">
  </picture>
</p>

---

## 📝 Project description

<p align="justify">
GitWit is a lightweight CLI tool designed to help developers craft better commit messages following semantic conventions. 
It provides interactive assistance (wizards), linting, and changelog generation, ensuring consistent, meaningful, and 
standardized commit history across projects. 
</p>

## 🤔 Problem definition

<p align="justify">
Writing clear, consistent, and structured commit messages can be challenging, especially in teams or open-source projects. 
GitWit addresses this by enforcing a configurable set of rules, guiding users either through an interactive wizard or 
automatic validation, reducing noise and improving project traceability.
</p>

## 🛠️ Technologies used

<p align="center">
    <img src="https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=gradle&logoColor=white" alt="Gradle"/>
    <img src="https://img.shields.io/badge/Java-ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java"/>
</p>

## 📦 Installation & Usage

### 📦 For git projects

GitWit is designed to work within projects that are version-controlled using Git.
You can use it in local repositories to:

- Create commits interactively using semantic commit conventions;
- Validate commit messages (linting);
- Generate changelogs based on commit history.

Download the latest JAR release:

```bash
curl -L https://github.com/rafandoo/gitwit/releases/latest/download/gitwit.jar -o gitwit.jar
```

You can now run commands like:

```bash
java -jar gitwit.jar -h              # Show help
java -jar gitwit.jar install         # Install GitWit alias in current repository
java -jar gitwit.jar commit          # Start interactive commit wizard
java -jar gitwit.jar lint            # Lint latest commit
java -jar gitwit.jar changelog       # Generate changelog for current branch
```

> ⚠️ Installers for Windows (.exe) and Linux (.deb/.sh) are under construction and will be provided soon.

### 🧪 Local Development

Clone the project and build it locally:

```bash
git clone https://github.com/rafandoo/gitwit.git
cd gitwit

./gradlew clean build
```

## 🔧 Functionalities

✔️ Interactive commit wizard – Step-by-step prompt for type, scope, short & long descriptions.
✔️ Commit linting – Validate commits individually or in ranges.
✔️ Changelog generation – Group commits by type and scope.
✔️ Custom YAML configuration – Define allowed types, scopes, and validation rules.
✔️ Multilingual support – Supports English and Portuguese messages.
✔️ Git hook friendly – Can be wired into Git lifecycle manually.

## 🚀 Future enhancements

✔️ Windows and Linux native installers.
✔️ Plugin support for custom lint rules.
✔️ Contribution metrics and insights.
✔️ Emoji support for commit types.
✔️ Support for commit signing.

## License 🔑

This project is licensed under the [Apache License 2.0](https://github.com/rafandoo/gitwit/blob/f76cb4e1e145816dc2cccd60c0ae0af6157172b0/LICENSE)

Copyright :copyright: 2025-present - Rafael Camargo
