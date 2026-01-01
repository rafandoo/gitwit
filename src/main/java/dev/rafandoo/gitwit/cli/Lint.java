package dev.rafandoo.gitwit.cli;

import dev.rafandoo.gitwit.config.GitWitConfig;
import dev.rafandoo.gitwit.service.LintService;
import dev.rafandoo.gitwit.service.MessageService;
import picocli.CommandLine;

/**
 * <h2>lint</h2>
 * <p>
 * Command used to validate one or more Git commits.
 * </p>
 *
 * <p>
 * If no range is provided, the most recent commit (HEAD) is checked. If a range is provided
 * using {@code --from} and {@code --to}, all commits in the interval will be validated.
 * </p>
 */
@CommandLine.Command(
    name = "lint",
    resourceBundle = "i18n.commands.lint",
    sortOptions = false
)
public class Lint extends BaseCommand {

    @CommandLine.Option(
        names = {"-f", "--from"},
        hidden = true
    )
    @Deprecated(forRemoval = true, since = "1.1.0")
    private String from;

    @CommandLine.Option(
        names = {"-t", "--to"},
        hidden = true
    )
    @Deprecated(forRemoval = true, since = "1.1.0")
    private String to;

    @CommandLine.Option(
        names = {"-m", "--message"},
        arity = "1..*",
        descriptionKey = "lint.option.message"
    )
    private String[] messageParts;

    @CommandLine.Parameters(
        index = "0",
        arity = "0..1",
        descriptionKey = "lint.parameter.rev-spec"
    )
    private String revSpec;

    @Override
    public void run() {
        GitWitConfig config = loadConfig();
        MessageService.getInstance().info("lint.start");
        LintService.getInstance().lint(
            this.revSpec,
            this.from,
            this.to,
            this.messageParts,
            config
        );
        MessageService.getInstance().success("lint.success");
    }
}
