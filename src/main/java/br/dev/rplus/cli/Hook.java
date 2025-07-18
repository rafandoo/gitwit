package br.dev.rplus.cli;

import br.dev.rplus.cli.wiz.CommitWizard;
import br.dev.rplus.entity.CommitMessage;
import br.dev.rplus.config.GitWitConfig;
import br.dev.rplus.enums.ExceptionMessage;
import br.dev.rplus.exception.GitWitException;
import br.dev.rplus.service.MessageService;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * <h2>hook</h2>
 * <p>
 * Flow:
 * <ol>
 *     <li>Loads the {@link GitWitConfig} from the configured YAML file.</li>
 *     <li>Starts the {@link CommitWizard} to collect and validate the commit message.</li>
 *     <li>Persists the formatted message to the file provided by Git so the
 *     commit can proceed.</li>
 * </ol>
 * If any validation fails, the process is aborted and a non-zero exit code is returned.
 */
@CommandLine.Command(
    name = "hook",
    hidden = true,
    description = "Launches the interactive commit wizard."
)
public class Hook extends BaseCommand {

    @CommandLine.Parameters(index = "0", description = "Path to the commit message file.")
    private Path messageFile;

    @Override
    public void run() {
        GitWitConfig config = loadConfig();

        // Start interactive wizard
        CommitMessage msg = new CommitWizard(config).run();

        // Persist the formatted message to the file provided by Git so the commit can proceed.
        try {
            Files.writeString(this.messageFile, msg.format());
        } catch (IOException e) {
            throw new GitWitException(ExceptionMessage.COMMIT_MSG_WRITE_FAILED, e);
        }
        MessageService.getInstance().debug("commit.wizard.message_written", this.messageFile);
        MessageService.getInstance().success("commit.wizard.message_success");
    }
}
