package dev.rafandoo.gitwit.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rafandoo.gitwit.config.GitWitConfig;
import dev.rafandoo.gitwit.entity.CommitMessage;
import dev.rafandoo.gitwit.exception.GitWitException;
import dev.rafandoo.gitwit.service.git.GitRepositoryService;
import lombok.AllArgsConstructor;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service responsible for linting commit messages.
 */
@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
public final class LintService {

    private final MessageService messageService;
    private final CommitMessageService commitMessageService;
    private final GitRepositoryService gitRepositoryService;

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
            this.commitMessageService.validate(commitMessage, config);
            this.messageService.success("lint.success");
            return;
        }

        List<RevCommit> commits = this.gitRepositoryService.resolveCommits(revSpec, from, to, config.getLint().getIgnored());
        if (commits.isEmpty()) {
            throw new GitWitException("lint.warn.no_commits");
        }
        Map<String, CommitMessage> messages = commits.stream()
            .collect(Collectors.toMap(
                commit -> commit.getId().getName(),
                CommitMessage::of
            ));

        this.messageService.debug("lint.total", messages.size());
        this.commitMessageService.validate(messages, config);
    }
}
