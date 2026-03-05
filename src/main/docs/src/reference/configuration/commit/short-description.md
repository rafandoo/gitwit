# Short description of the commit

Sets the validation rules for the short description of the commit, which represents the main summary of the change made.

This field is required and corresponds to the summary message displayed in the commit history, being used to quickly
identify the purpose of the change.

```yaml
shortDescription:
  description: "Please provide a brief summary"
  minLength: 5
  maxLength: 70
```

| Field         | Required | Type   | Standard | Description                                                                                                                              |
|---------------|----------|--------|----------|------------------------------------------------------------------------------------------------------------------------------------------|
| `description` | No       | String | -        | Text displayed as auxiliary instruction in the interactive wizard (_wizard_), guiding the user on how to fill out the short description. |
| `minLength`   | No       | Int    | 1        | Set the minimum number of characters allowed for the short description of the commit.                                                    |
| `maxLength`   | No       | Int    | 72       | Sets the maximum character limit allowed for short description, helping to keep messages concise and readable in history.                |
