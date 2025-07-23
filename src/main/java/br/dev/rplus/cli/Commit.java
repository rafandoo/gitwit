package br.dev.rplus.cli;

import br.dev.rplus.cli.wiz.CommitWizard;
import br.dev.rplus.config.GitWitConfig;
import br.dev.rplus.cup.utils.StringUtils;
import br.dev.rplus.entity.CommitMessage;
import br.dev.rplus.service.CommitMessageService;
import br.dev.rplus.service.GitService;
import br.dev.rplus.service.MessageService;
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
    description = "Launches the interactive commit wizard or performs a direct commit using provided options."
)
public class Commit extends BaseCommand {

    @CommandLine.Option(
        names = {"-a", "--add"},
        description = "Automatically stage all modified and untracked files before committing."
    )
    private boolean add;

    @CommandLine.Option(
        names = {"-t", "--type"},
        description = "Type of the commit (e.g., feat fix chore)."
    )
    private String type;

    @CommandLine.Option(
        names = {"-s", "--scope"},
        description = "Scope of the commit (e.g., core, ui, auth)."
    )
    private String scope;

    @CommandLine.Option(
        names = {"-d", "--description"},
        description = "Short description of the commit."
    )
    private String shortDescription;

    @CommandLine.Option(
        names = {"-l", "--long-description"},
        description = "Detailed (multi-line) description of the commit."
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
                null
            );
            CommitMessageService.getInstance().validate(message, config);
        } else {
            // Start interactive wizard
            message = new CommitWizard(config).run();
        }

        // Perform the commit
        RevCommit commit = GitService.getInstance().commit(message, this.add);

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
