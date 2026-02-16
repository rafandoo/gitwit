package dev.rafandoo.gitwit.cli;

import com.google.inject.Inject;
import dev.rafandoo.gitwit.cli.dto.ChangelogOptions;
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

    @CommandLine.ArgGroup(exclusive = false)
    private ChangelogOptions options;

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
        if (this.options == null && this.revSpec == null) {
            super.run();
            return;
        }

        if (this.options == null) {
            this.options = new ChangelogOptions();
        }

        GitWitConfig config = loadConfig();
        messageService.info("changelog.start");

        this.changelogService.handle(
            this.revSpec,
            this.options,
            config
        );
    }
}
