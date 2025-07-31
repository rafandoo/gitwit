<h1 align="center">GitWit</h1>

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

## ⚙️ Configurations

You can customize GitWit using a `.gitwit` file placed in your repository root or home directory.

To generate an example file:

```bash
java -jar gitwit.jar --config-example
```

### ✅ Summary of config options

| Key                                        | Required | Type    |             Default value              | Description                                                                                       |
|--------------------------------------------|:--------:|---------|:--------------------------------------:|---------------------------------------------------------------------------------------------------|
| `types.description`                        |    ❌     | String  |                   -                    | Optional description to help users understand how to use the commit type                          |
| `types.values`                             |    ✔️    | List    |                   -                    | List of allowed types for commits                                                                 |
| `scope.description`                        |    ❌     | String  |                   -                    | Optional description to help users understand the scope usage                                     |
| `scope.required`                           |    ❌     | Boolean |                 False                  | Defines whether the scope is required or not                                                      |
| `scope.type`                               |    ✔️    | String  |                  text                  | Defines if the scope field is open text (`text`) or a predefined list (`list`)                    |
| `scope.values`                             |    ❌     | List    |                   -                    | List of allowed scopes to organize commits (optional but recommended when `scope.type` is `list`) |
| `shortDescription.description`             |    ❌     | String  |                   -                    | Optional description to help users fill in the short description                                  |
| `shortDescription.minLength`               |    ❌     | Int     |                   1                    | Minimum number of characters allowed in the short description                                     |
| `shortDescription.maxLength`               |    ❌     | Int     |                   72                   | Maximum number of characters allowed in the short description                                     |
| `longDescription.enabled`                  |    ❌     | Boolean |                 False                  | Enables the long description field                                                                |
| `longDescription.description`              |    ❌     | String  |                   -                    | Optional description to help users fill in the long description                                   |
| `longDescription.required`                 |    ❌     | Boolean |                 False                  | Defines whether the long description is required                                                  |
| `longDescription.minLength`                |    ❌     | Int     |                   0                    | Minimum number of characters allowed in the long description                                      |
| `longDescription.maxLength`                |    ❌     | Int     |                  100                   | Maximum number of characters allowed in the long description                                      |
| `changelog.title`                          |    ❌     | String  |               Changelog                | Title to be used at the top of the generated changelog                                            |
| `changelog.types`                          |    ✔️    | List    |                   -                    | List of commit types and their titles to group entries when generating changelogs                 |
| `changelog.showOtherTypes`                 |    ❌     | Boolean |                  True                  | Whether to include other commit types not explicitly listed in `changelog.types`                  |
| `changelog.showBreakingChanges`            |    ❌     | Boolean |                 False                  | Whether breaking changes should be displayed in a separate section                                |
| `changelog.ignored`                        |    ❌     | List    |                   -                    | List of commit types to be ignored in changelog generation                                        |
| `changelog.format.sectionTemplate`         |    ❌     | String  |                   -                    | Template used for commits that are in a changelog section (defined in 'types')                    |
| `changelog.format.breakingChangesTemplate` |    ❌     | String  |                   -                    | Template used for breaking changes (regardless of type)                                           |
| `changelog.format.otherTypesTemplate`      |    ❌     | String  |                   -                    | Template used for types that are not listed in 'types'                                            |
| `changelog.format.defaultTemplate`         |    ❌     | String  | "{scope}: {description} ({shortHash})" | Generic fallback if the others do not apply                                                       |

## 🔧 Functionalities

✔️ Interactive commit wizard – Step-by-step prompt for type, scope, short & long descriptions.

✔️ Commit linting – Validate commits individually or in ranges.

✔️ Changelog generation – Group commits by type and scope.

✔️ Custom YAML configuration – Define allowed types, scopes, and validation rules.

✔️ Multilingual support – Supports English and Portuguese messages.

✔️ Git hook friendly – Can be wired into Git lifecycle manually.

✔️ Emoji support for commit types.

## 🚀 Future enhancements

✔️ Windows and Linux native installers.

✔️ Plugin support for custom lint rules.

✔️ Contribution metrics and insights.

✔️ Support for commit signing.

## License 🔑

This project is licensed under the [Apache License 2.0](https://github.com/rafandoo/gitwit/blob/f76cb4e1e145816dc2cccd60c0ae0af6157172b0/LICENSE)

Copyright :copyright: 2025-present - Rafael Camargo
