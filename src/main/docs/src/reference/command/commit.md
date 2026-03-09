# Commit

Starts the interactive wizard for creating standardized commits or lets you report commit data directly via command-line
parameters.

The command guides the user in building the commit message according to the rules configured in the project, ensuring
consistency and automatic validation.

## Use

```bash
gitwit commit [-aem] [-t=<type>] [-s=<scope>] [-d=<shortDescription>] [-l=<longDescription>] 
```

| Option                                     | Description                                                                                                    |
|--------------------------------------------|----------------------------------------------------------------------------------------------------------------|
| `-a, --add`                                | Automatically adds all modified and untracked files before creating the commit.                                |
| `-m, --amend`                              | Change the last existing commit instead of creating a new commit.                                              |
| `-e, --allow-empty`                        | Allows the creation of commits without changes in files.                                                       |
| `-t, --type=<type>`                        | Define the type of the commit (e.g. `feat`, `fix`, `chore`), according to the types configured in the project. |
| `-s, --scope=<scope>`                      | Define the scope of the change by indicating the affected area or module (e.g. `core`, `ui`, `auth`).          |
| `-d, --description=<shortDescription>`     | Define the short description of the commit, used as a master summary of the change.                            |
| `-l, --long-description=<longDescription>` | Define the commit’s long description, used to add additional details about the change.                         |

## Examples

```bash
# Start the interactive wizard
gitwit commit

# Creates a commit by reporting the data directly by parameters
gitwit commit -t feat -s core -d "adds support for logs"

# Updates the last commit and automatically adds modified files
gitwit commit --amend --add -t fix -s api -d "fix JWT authentication"
```
