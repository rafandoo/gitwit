package dev.rafandoo.gitwit.cli;

import dev.rafandoo.cup.utils.StringUtils;
import dev.rafandoo.gitwit.config.GitWitConfig;
import dev.rafandoo.gitwit.entity.CommitMessage;
import dev.rafandoo.gitwit.service.CommitMessageService;
import dev.rafandoo.gitwit.service.GitService;
import dev.rafandoo.gitwit.service.MessageService;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.revwalk.RevCommit;
import picocli.CommandLine;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @CommandLine.Parameters(
        index = "0",
        arity = "0",
        descriptionKey = "lint.parameter.rev-spec"
    )
    private String revSpec;

    @Override
    public void run() {
        GitWitConfig config = loadConfig();

        MessageService.getInstance().info("lint.start");

        List<RevCommit> commits = this.resolveCommits();
        Map<String, CommitMessage> messages = commits.stream()
            .collect(Collectors.toMap(
                commit -> commit.getId().getName(),
                CommitMessage::of
            ));

        MessageService.getInstance().debug("lint.total", messages.size());
        CommitMessageService.getInstance().validate(messages, config);
        MessageService.getInstance().success("lint.success");
    }

    /**
     * Resolves the commits to be linted based on the provided revision specification.
     *
     * @return list of {@link RevCommit} objects to be linted.
     */
    private List<RevCommit> resolveCommits() {
        GitService git = GitService.getInstance();

        if (!StringUtils.isNullOrBlank(revSpec)) {
            if (revSpec.contains("..")) {
                String[] parts = revSpec.split("\\.\\.", 2);
                return git.listCommitsBetween(parts[0], parts[1]);
            }
            return git.resolveCommit(revSpec).stream().toList();
        } else if (!StringUtils.isNullOrBlank(from) || !StringUtils.isNullOrBlank(to)) {
            MessageService.getInstance().warn("lint.deprecated-range-options");
            return git.listCommitsBetween(from, to);
        }

        return git.resolveCommit(Constants.HEAD).stream().toList();
    }

}
