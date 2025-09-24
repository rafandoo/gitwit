package dev.rafandoo.gitwit.cli;

import dev.rafandoo.gitwit.config.GitWitConfig;
import dev.rafandoo.gitwit.entity.CommitMessage;
import dev.rafandoo.gitwit.service.CommitMessageService;
import dev.rafandoo.gitwit.service.GitService;
import dev.rafandoo.gitwit.service.MessageService;
import org.eclipse.jgit.revwalk.RevCommit;
import picocli.CommandLine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        descriptionKey = "lint.option.from"
    )
    private String from;

    @CommandLine.Option(
        names = {"-t", "--to"},
        descriptionKey = "lint.option.to"
    )
    private String to;

    @Override
    public void run() {
        GitWitConfig config = loadConfig();

        if (this.from == null) {
            MessageService.getInstance().info("lint.start_head");
        } else if (this.to == null) {
            MessageService.getInstance().info("lint.start_from", this.from);
        } else {
            MessageService.getInstance().info("lint.start_multiple", this.from, this.to);
        }

        // Prepare commit messages map
        Map<String, CommitMessage> messages = new HashMap<>();
        List<RevCommit> commits = GitService.getInstance().getCommits(from, to);

        commits.forEach(
            commit -> messages.put(commit.getId().getName(), CommitMessage.of(commit))
        );

        MessageService.getInstance().debug("lint.total_messages", messages.size());

        // Validate all collected commit messages
        CommitMessageService.getInstance().validate(messages, config);

        MessageService.getInstance().success("lint.success");
    }
}
