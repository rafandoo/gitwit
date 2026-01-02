package dev.rafandoo.gitwit.service;

import dev.rafandoo.cup.utils.StringUtils;
import dev.rafandoo.gitwit.config.GitWitConfig;
import dev.rafandoo.gitwit.entity.CommitMessage;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service responsible for linting commit messages.
 */
public final class LintService {

    private static LintService instance;

    /**
     * Private constructor to prevent instantiation.
     */
    private LintService() {
    }

    /**
     * Returns the singleton instance, instantiating it on first use.
     *
     * @return {@link LintService} instance.
     */
    public static synchronized LintService getInstance() {
        if (instance == null) {
            instance = new LintService();
        }
        return instance;
    }

    /**
     * Lints commit messages based on the provided revision specification or message parts.
     *
     * @param revSpec      revision specification (e.g., "HEAD~5..HEAD").
     * @param from         starting point of the commit range (deprecated).
     * @param to           ending point of the commit range (deprecated).
     * @param messageParts parts of a single commit message to lint.
     * @param config       GitWit configuration.
     */
    public void lint(String revSpec, String from, String to, String[] messageParts, GitWitConfig config) {
        if (messageParts != null) {
            String rawMessage = String.join(" ", messageParts);
            CommitMessage commitMessage = CommitMessage.of(rawMessage);
            CommitMessageService.getInstance().validate(commitMessage, config);
            MessageService.getInstance().success("lint.success");
            return;
        }

        List<RevCommit> commits = this.resolveCommits(revSpec, from, to, config);
        Map<String, CommitMessage> messages = commits.stream()
            .collect(Collectors.toMap(
                commit -> commit.getId().getName(),
                CommitMessage::of
            ));

        MessageService.getInstance().debug("lint.total", messages.size());
        CommitMessageService.getInstance().validate(messages, config);
    }

    /**
     * Resolves the commits to be linted based on the provided revision specification.
     *
     * @param revSpec revision specification (e.g., "HEAD~5..HEAD").
     * @param from    starting point of the commit range (deprecated).
     * @param to      ending point of the commit range (deprecated).
     * @param config  GitWit configuration.
     * @return list of {@link RevCommit} objects to be linted.
     */
    private List<RevCommit> resolveCommits(String revSpec, String from, String to, GitWitConfig config) {
        GitService git = GitService.getInstance();
        List<RevCommit> commits;

        if (!StringUtils.isNullOrBlank(revSpec)) {
            if (revSpec.contains("..")) {
                String[] parts = revSpec.split("\\.\\.", 2);
                commits = git.listCommitsBetween(parts[0], parts[1]);
            } else {
                commits = git.resolveCommit(revSpec).stream().collect(Collectors.toList());
            }
        } else if (!StringUtils.isNullOrBlank(from) || !StringUtils.isNullOrBlank(to)) {
            MessageService.getInstance().warn("lint.deprecated-range-options");
            commits = git.listCommitsBetween(from, to);
        } else {
            commits = git.resolveCommit(Constants.HEAD).stream().collect(Collectors.toList());
        }

        if (config.getLint().getIgnored() != null) {
            commits.removeIf(commit -> config.getLint()
                .getIgnored()
                .stream()
                .anyMatch(ignored -> commit.getFullMessage().matches(ignored))
            );
        }

        return commits;
    }
}
