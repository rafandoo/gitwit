package br.dev.rplus.service;

import br.dev.rplus.config.GitWitConfig;
import br.dev.rplus.cup.utils.StringUtils;
import br.dev.rplus.entity.CommitMessage;
import br.dev.rplus.enums.ChangelogScope;
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
     * @return a {@link StringBuilder} containing the generated changelog.
     * @throws GitWitException if changelog generation fails due to configuration or I/O errors.
     */
    public StringBuilder generateChangelog(String from, String to, GitWitConfig config) {
        Map<String, String> types = config.getChangelog().getTypes();
        if (types == null) {
            throw new GitWitException(ExceptionMessage.CHANGELOG_TYPES_REQUIRED);
        }

        List<String> ignored = Objects.requireNonNullElse(
            config.getChangelog().getIgnored(), new ArrayList<>()
        );

        List<RevCommit> commits = GitService.getInstance()
            .getCommits(from, Objects.requireNonNullElse(to, Constants.HEAD));

        List<CommitMessage> commitMessages = new ArrayList<>();
        commits.forEach(commit -> commitMessages.add(CommitMessage.of(commit)));

        Map<String, List<CommitMessage>> groupedByType = commitMessages.stream()
            .filter(commitMessage -> !ignored.contains(commitMessage.type()))
            .collect(Collectors.groupingBy(CommitMessage::type));

        return this.generateMarkdown(config, groupedByType, types);
    }

    /**
     * Generates a Markdown-formatted changelog based on grouped commit messages.
     *
     * @param config        the configuration settings for changelog generation
     * @param groupedByType a map of commit messages grouped by their type
     * @param types         a map of commit type keys to their display titles
     * @return a {@link StringBuilder} containing the generated Markdown changelog, or {@code null} if no commits are found
     */
    private StringBuilder generateMarkdown(GitWitConfig config, Map<String, List<CommitMessage>> groupedByType, Map<String, String> types) {
        if (groupedByType.isEmpty()) {
            MessageService.getInstance().warn("warn.changelog_no_commits");
            return null;
        }

        StringBuilder sb = new StringBuilder();
        Heading heading = new Heading(config.getChangelog().getTitle(), 1);
        heading.setUnderlineStyle(false);
        sb.append(heading).append("\n\n");

        if (config.getChangelog().isShowBreakingChanges()) {
            sb.append(new Heading("Breaking Changes", 3)).append("\n\n");
            List<String> allBreakingChanges = new ArrayList<>();

            groupedByType.forEach((type, commitMessages) -> {
                List<CommitMessage> breakingChanges = commitMessages.stream()
                    .filter(CommitMessage::breakingChanges)
                    .toList();

                commitMessages.removeAll(breakingChanges);

                allBreakingChanges.addAll(
                    breakingChanges.stream()
                        .map(message -> message.formatForChangelog(config.getChangelog().getFormat(), ChangelogScope.BREAKING_CHANGES))
                        .toList()
                );
            });
            if (!allBreakingChanges.isEmpty()) {
                sb.append(new UnorderedList<>(allBreakingChanges)).append("\n\n");
            }
        }

        types.forEach((typeKey, typeTitle) -> {
            if (groupedByType.containsKey(typeKey)) {
                List<String> messages = groupedByType.get(typeKey).stream()
                    .map(message -> message.formatForChangelog(config.getChangelog().getFormat(), ChangelogScope.SECTION))
                    .collect(Collectors.toList());
                if (!messages.isEmpty()) {
                    sb.append(new Heading(typeTitle, 3)).append("\n\n");
                    sb.append(new UnorderedList<>(messages)).append("\n\n");
                }

                groupedByType.remove(typeKey);
            }
        });

        if (config.getChangelog().isShowOtherTypes()) {
            sb.append(new Heading(I18nService.getInstance().getMessage("changelog.other_changes"), 3))
                .append("\n\n");
            sb.append(new UnorderedList<>(
                groupedByType.values()
                    .stream()
                    .flatMap(List::stream)
                    .map(message -> message.formatForChangelog(config.getChangelog().getFormat(), ChangelogScope.OTHER_TYPES))
                    .toList()
            ));
        }

        return sb;
    }

    /**
     * Writes a changelog file based on grouped commit messages and predefined values.
     *
     * @param content the content to be written to the changelog file.
     * @return the {@link Path} to the generated changelog file.
     * @throws IOException if there is an error creating or writing to the changelog file.
     */
    public Path writeChangeLog(String content) throws IOException {
        Path changelogFile = this.getChangelogFile();

        if (!Files.exists(changelogFile)) {
            Files.createFile(changelogFile);
        }

        Files.writeString(changelogFile, content);
        return changelogFile;
    }

    /**
     * Retrieves the commit message template for a specific changelog format and scope.
     *
     * @param format the changelog format configuration.
     * @param scope  the scope of the changelog (e.g., section, breaking changes, other types).
     * @return the commit message template as a {@link String}.
     * @throws GitWitException if no template is defined for the specified scope and no default template is available.
     */
    public String getChangelogCommitTemplateByScope(GitWitConfig.ChangelogConfig.ChangelogFormat format, ChangelogScope scope) {
        String template = switch (scope) {
            case SECTION -> format.getSectionTemplate();
            case BREAKING_CHANGES -> format.getBreakingChangesTemplate();
            case OTHER_TYPES -> format.getOtherTypesTemplate();
        };

        if (StringUtils.isNullOrBlank(template)) {
            String defaultTemplate = format.getDefaultTemplate();
            if (!StringUtils.isNullOrBlank(defaultTemplate)) {
                return defaultTemplate;
            }
            throw new GitWitException(ExceptionMessage.CHANGELOG_NO_TEMPLATE_DEFINED);
        }
        return template;
    }
}
