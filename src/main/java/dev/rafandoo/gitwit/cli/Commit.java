package dev.rafandoo.gitwit.cli;

import dev.rafandoo.gitwit.cli.wiz.CommitWizard;
import dev.rafandoo.gitwit.config.GitWitConfig;
import dev.rafandoo.cup.utils.StringUtils;
import dev.rafandoo.gitwit.entity.CommitMessage;
import dev.rafandoo.gitwit.service.CommitMessageService;
import dev.rafandoo.gitwit.service.GitService;
import dev.rafandoo.gitwit.service.MessageService;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.revwalk.RevCommit;
import picocli.CommandLine;

/**
 * <h2>commit</h2>
 * <p>
 * Command that launches the interactive commit wizard or performs a commit directly
 * using the provided options.
 * </p>
 *
 * <h3>Execution Flow</h3>
 * <ol>
 *     <li>Loads the {@link GitWitConfig} from the YAML configuration file.</li>
 *     <li>If the commit type is provided via CLI, it builds and validates the commit message directly.</li>
 *     <li>Otherwise, it starts the {@link CommitWizard} to guide the user interactively.</li>
 *     <li>Executes the commit using the Git API via {@link GitService}.</li>
 * </ol>
 *
 * <p>
 * If validation fails or the commit cannot be performed, an error message is shown and the process exits with a non-zero status.
 * </p>
 */
@CommandLine.Command(
    name = "commit",
    resourceBundle = "i18n.commands.commit",
    sortOptions = false
)
public class Commit extends BaseCommand {

    @CommandLine.Option(
        names = {"-a", "--add"},
        descriptionKey = "commit.option.add"
    )
    private boolean add;

    @CommandLine.Option(
        names = {"-am", "--amend"},
        descriptionKey = "commit.option.amend"
    )
    private boolean amend;

    @CommandLine.Option(
        names = {"-t", "--type"},
        descriptionKey = "commit.option.type"
    )
    private String type;

    @CommandLine.Option(
        names = {"-s", "--scope"},
        descriptionKey = "commit.option.scope"
    )
    private String scope;

    @CommandLine.Option(
        names = {"-d", "--description"},
        descriptionKey = "commit.option.short-description"
    )
    private String shortDescription;

    @CommandLine.Option(
        names = {"-l", "--long-description"},
        descriptionKey = "commit.option.long-description"
    )
    private String longDescription;

    @Override
    public void run() {
        GitWitConfig config = loadConfig();

        CommitMessage message;
        if (!StringUtils.isNullOrBlank(this.type)) {
            // Commit via CLI input
            message = new CommitMessage(
                this.type,
                this.scope,
                this.shortDescription,
                this.longDescription,
                false,
                null,
                null,
                null
            );
            CommitMessageService.getInstance().validate(message, config);
        } else {
            // Start interactive wizard
            message = new CommitWizard(config).run();
        }

        // Perform the commit
        RevCommit commit = GitService.getInstance().commit(message, this.add, this.amend);

        if (commit != null) {
            MessageService.getInstance().success(
                "commit.execution.success",
                commit.getId().abbreviate(Constants.OBJECT_ID_ABBREV_STRING_LENGTH).name()
            );
        } else {
            MessageService.getInstance().error("commit.execution.failed");
        }
    }
}
