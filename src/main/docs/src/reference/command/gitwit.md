# Main Command

The main command of GitWit allows you to access the tool’s features and execute its subcommands.
Also provides global options such as help view, installed version, sample configuration generation and mode
of purification.

## Use

```bash
gitwit [-dhV] [-ce] [COMMAND]
```

| Option                  | Description                                                                                                    |
|-------------------------|----------------------------------------------------------------------------------------------------------------|
| `-d, --debug`           | Enables debug mode, displaying additional information during command execution.                                |
| `-ce, --config-example` | Generates an example `.gitwit` file in the current directory, containing an initial configuration of the tool. |
| `-h, --help`            | Displays GitWit’s general help listing the available commands and their options.                               |
| `-V, --version`         | Displays the currently installed version of GitWit.                                                            |
