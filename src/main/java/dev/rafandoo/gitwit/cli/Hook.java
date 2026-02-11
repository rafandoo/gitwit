package dev.rafandoo.gitwit.cli;

import com.google.inject.Inject;
import dev.rafandoo.gitwit.cli.wiz.CommitWizard;
import dev.rafandoo.gitwit.entity.CommitMessage;
import dev.rafandoo.gitwit.config.GitWitConfig;
import dev.rafandoo.gitwit.exception.GitWitException;
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

    @Inject
    private CommitWizard commitWizard;

    @Override
    public void run() {
        GitWitConfig config = loadConfig();

        // Start interactive wizard
        CommitMessage msg = this.commitWizard.run(config);

        // Persist the formatted message to the file provided by Git so the commit can proceed.
        try {
            Files.writeString(this.messageFile, msg.format());
        } catch (IOException e) {
            throw new GitWitException("commit.hook.error.commit_write", e);
        }
        messageService.debug("commit.hook.written", this.messageFile);
        messageService.success("commit.hook.success");
    }
}
