package dev.rafandoo.gitwit.cli;

import com.google.inject.Inject;
import dev.rafandoo.gitwit.config.GitWitConfig;
import dev.rafandoo.gitwit.service.changelog.ChangelogService;
import picocli.CommandLine;

/**
 * <h2>changelog</h2>
 * <p>
 * Generates a changelog based on the commit history of the Git repository.
 * </p>
 *
 * <p>
 * The changelog is generated based on the commit messages and the configuration provided in the GitWit config file.
 * </p>
 */
@CommandLine.Command(
    name = "changelog",
    resourceBundle = "i18n.commands.changelog",
    sortOptions = false
)
public class Changelog extends BaseCommand {

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
        names = {"-c", "--copy"},
        descriptionKey = "changelog.option.copy"
    )
    private boolean copyToClipboard;

    @CommandLine.Option(
        names = {"-s", "--subtitle"},
        descriptionKey = "changelog.option.subtitle"
    )
    private String subtitle;

    @CommandLine.Option(
        names = {"-a", "--append"},
        descriptionKey = "changelog.option.append"
    )
    private boolean append = false;

    @CommandLine.Parameters(
        index = "0",
        arity = "0..1",
        descriptionKey = "changelog.parameter.rev-spec"
    )
    private String revSpec;

    @Inject
    private ChangelogService changelogService;

    @Override
    public void run() {
        GitWitConfig config = loadConfig();
        messageService.info("changelog.start");

        this.changelogService.handle(
            revSpec,
            from,
            to,
            config,
            subtitle,
            copyToClipboard,
            append
        );
    }
}
