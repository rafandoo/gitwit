package br.dev.rplus.service;

import br.dev.rplus.config.GitWitConfig;
import br.dev.rplus.entity.CommitMessage;
import br.dev.rplus.enums.ConfigPaths;
import br.dev.rplus.enums.ExceptionMessage;
import br.dev.rplus.exception.GitWitException;
import net.steppschuh.markdowngenerator.list.UnorderedList;
import net.steppschuh.markdowngenerator.text.heading.Heading;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Service responsible for generating and managing changelogs based on Git commit history.
 * <p>
 * This singleton service provides methods to create changelogs by processing commit messages,
 * grouping them by type, and generating a Markdown-formatted changelog file.
 */
public final class ChangelogService {

    private static ChangelogService instance;

    /**
     * Private constructor to prevent instantiation.
     */
    private ChangelogService() {
    }

    /**
     * Returns the singleton instance, instantiating it on first use.
     *
     * @return {@link ChangelogService} instance.
     */
    public static synchronized ChangelogService getInstance() {
        if (instance == null) {
            instance = new ChangelogService();
        }
        return instance;
    }

    /**
     * Retrieves the path to the changelog file within the current Git repository.
     *
     * @return a {@link Path} representing the location of the changelog file.
     */
    private Path getChangelogFile() {
        return GitService.getInstance()
            .getRepo()
            .resolve(ConfigPaths.CHANGELOG_FILE.get().asString());
    }

    /**
     * Generates a changelog based on Git commits between two references.
     *
     * @param from   the starting Git reference for commit range.
     * @param to     the ending Git reference for commit range (defaults to HEAD if {@code null}).
     * @param config the configuration settings for changelog generation.
     * @return the {@link Path} to the generated changelog file.
     * @throws GitWitException if changelog generation fails due to configuration or I/O errors.
     */
    public Path generateChangelog(String from, String to, GitWitConfig config) {
        Map<String, String> values = config.getChangelog().getValues();
        if (values == null) {
            throw new GitWitException(ExceptionMessage.CHANGELOG_VALUES_REQUIRED);
        }

        List<String> ignored = Objects.requireNonNullElse(
            config.getChangelog().getIgnored(), new ArrayList<>()
        );

        Iterable<RevCommit> commits = GitService.getInstance()
            .getCommits(from, Objects.requireNonNullElse(to, Constants.HEAD));

        List<CommitMessage> commitMessages = new ArrayList<>();
        commits.forEach(commit -> commitMessages.add(CommitMessage.of(commit)));

        Map<String, List<CommitMessage>> groupedByType = commitMessages.stream()
            .filter(commitMessage -> !ignored.contains(commitMessage.type()))
            .collect(Collectors.groupingBy(CommitMessage::type));

        try {
            return this.writeChangeLog(groupedByType, values);
        } catch (IOException e) {
            throw new GitWitException(ExceptionMessage.CHANGELOG_FAILURE_WRITE, e);
        }
    }

    /**
     * Writes a changelog file based on grouped commit messages and predefined values.
     *
     * @param groupedByType a map of commit types to their corresponding commit messages.
     * @param values        a map of commit type keys to their display titles.
     * @return the {@link Path} to the generated changelog file, or {@code null} if no commits are found.
     * @throws IOException if there is an error creating or writing to the changelog file.
     */
    private Path writeChangeLog(Map<String, List<CommitMessage>> groupedByType, Map<String, String> values) throws IOException {
        Path changelogFile = this.getChangelogFile();

        if (!Files.exists(changelogFile)) {
            Files.createFile(changelogFile);
        }

        if (groupedByType.isEmpty()) {
            MessageService.getInstance().warn("warn.changelog_no_commits");
            return null;
        }

        StringBuilder sb = new StringBuilder();
        Heading heading = new Heading("Changelog", 1);
        heading.setUnderlineStyle(false);
        sb.append(heading).append("\n\n");

        values.forEach((typeKey, title) -> {
            if (groupedByType.containsKey(typeKey)) {
                List<String> messages = groupedByType.get(typeKey).stream()
                    .map(CommitMessage::formatForChangelog)
                    .collect(Collectors.toList());
                if (!messages.isEmpty()) {
                    sb.append(new Heading(title, 3)).append("\n\n");
                    sb.append(new UnorderedList<>(messages)).append("\n\n");
                }

                groupedByType.remove(typeKey);
            }
        });

        sb.append(new Heading(I18nService.getInstance().getMessage("changelog.other_changes"), 3))
            .append("\n\n");
        sb.append(new UnorderedList<>(
            groupedByType.values()
                .stream()
                .flatMap(List::stream)
                .map(CommitMessage::formatForChangelogOthers)
                .collect(Collectors.toList())
        ));

        Files.writeString(changelogFile, sb.toString());
        return changelogFile;
    }
}
