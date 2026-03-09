# Long description of the commit

Sets up an optional field to add a detailed description to the commit, allowing you to supplement the main summary with
more context about the change made.

This field may be used to explain motivations, technical decisions or relevant additional information that does not fit
in the short description.

```yaml
longDescription:
  enabled: true
  description: "Additional details"
  required: false
  minLength: 20
  maxLength: 100
```

| Field         | Required | Type    | Default | Description                                                                                                                      |
|---------------|----------|---------|---------|----------------------------------------------------------------------------------------------------------------------------------|
| `enabled`     | No       | Boolean | false   | Enables or disables the long description field during commit creation.                                                           |
| `description` | No       | String  | -       | Text displayed as auxiliary instruction in the interactive assistant (_wizard_), guiding the completion of the long description. |
| `required`    | No       | Boolean | false   | Sets whether long description completion is mandatory when the field is enabled.                                                 |
| `minLength`   | No       | Int     | 0       | Minimum number of characters allowed for long description.                                                                       |
| `maxLength`   | No       | Int     | 100     | Maximum number of characters allowed for long description.                                                                       |
