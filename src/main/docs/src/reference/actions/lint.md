# Lint

The **lint** command validates commit messages using the rules configured in GitWit.

This validation helps maintain a consistent commit history, ensuring that all messages follow the project-defined pattern and remain compatible with features like automatic changelog generation.

## Example of use

```yaml
name: GitWit CI

on:
  pull_request:
    branches:
      - main

jobs:
  gitwit:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v6
        with:
          fetch-depth: 0

      - name: Run GitWit Lint
        uses: rafandoo/gitwit-action@v1
        with:
          command: lint
```

## What happens

During workflow execution, the action will:

1. Automatically detect the Pull Request commit interval;
2. Run GitWit within the action environment;
3. Validate all the found commit messages;
4. Fail the workflow if any commit does not meet the linting rules configured.

## Error example

If a commit message does not follow the expected pattern, the workflow is paused and displays a detailed error indicating which commits violated the rules, as in the example below:

```txt
ERROR: following violations were found:
 - 835696e55db9d29b362f61a39ff4f653cec6ffe7:
    - Commit type: The specified commit type is not allowed, check the configuration file. Provided value: invalid.
```

## Good practices

It is recommended to use the `lint` in:

- Pull Requests;
- Continuous integration (CI) pipelines;
- Automatic validations before merge.

This ensures that project commits remain standardized and compatible with the defined automation flow.
