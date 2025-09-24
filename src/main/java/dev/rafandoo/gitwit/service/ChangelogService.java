package dev.rafandoo.gitwit.service;

import dev.rafandoo.gitwit.config.GitWitConfig;
import br.dev.rplus.cup.utils.StringUtils;
import dev.rafandoo.gitwit.entity.CommitMessage;
import dev.rafandoo.gitwit.enums.ChangelogScope;
import dev.rafandoo.gitwit.enums.ConfigPaths;
import dev.rafandoo.gitwit.enums.ExceptionMessage;
import dev.rafandoo.gitwit.exception.GitWitException;
import dev.rafandoo.gitwit.util.EmojiUtil;
import net.steppschuh.markdowngenerator.list.UnorderedList;
import net.steppschuh.markdowngenerator.text.heading.Heading;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
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
     * @param from     the starting Git reference for commit range.
     * @param to       the ending Git reference for commit range (defaults to HEAD if {@code null}).
     * @param config   the configuration settings for changelog generation.
     * @param subtitle an optional subtitle for the changelog. e.g. "Release Notes" or "Version 1.0.0".
     * @return a {@link StringBuilder} containing the generated changelog.
     * @throws GitWitException if changelog generation fails due to configuration or I/O errors.
     */
    public StringBuilder generateChangelog(String from, String to, GitWitConfig config, String subtitle) {
        Map<String, String> types = config.getChangelog()
            .getTypes()
            .entrySet()
            .stream()
            .collect(Collectors.toMap(
                entry -> EmojiUtil.replaceEmojiWithAlias(entry.getKey()),
                Map.Entry::getValue
            ));

        if (types.isEmpty()) {
            throw new GitWitException(ExceptionMessage.CHANGELOG_TYPES_REQUIRED);
        }

        List<String> ignored = Objects.requireNonNullElse(
                config.getChangelog().getIgnored(),
                new ArrayList<String>()
            )
            .stream()
            .map(EmojiUtil::replaceEmojiWithAlias)
            .toList();

        List<RevCommit> commits = GitService.getInstance()
            .getCommits(from, Objects.requireNonNullElse(to, Constants.HEAD));

        List<CommitMessage> commitMessages = new ArrayList<>();
        commits.forEach(commit -> commitMessages.add(CommitMessage.of(commit)));

        Map<String, List<CommitMessage>> groupedByType = commitMessages.stream()
            .filter(commitMessage -> !ignored.contains(commitMessage.type()))
            .collect(Collectors.groupingBy(CommitMessage::type));

        return this.generateMarkdown(config, groupedByType, types, subtitle);
    }

    /**
     * Generates a Markdown-formatted changelog based on grouped commit messages.
     *
     * @param config        the configuration settings for changelog generation.
     * @param groupedByType a map of commit messages grouped by their type.
     * @param types         a map of commit type keys to their display titles.
     * @param subtitle      an optional subtitle for the changelog, e.g., "Release Notes" or "Version 1.0.0".
     * @return a {@link StringBuilder} containing the generated Markdown changelog, or {@code null} if no commits are found.
     */
    private StringBuilder generateMarkdown(
        GitWitConfig config,
        Map<String, List<CommitMessage>> groupedByType,
        Map<String, String> types,
        String subtitle
    ) {
        if (groupedByType.isEmpty()) {
            MessageService.getInstance().warn("warn.changelog_no_commits");
            return null;
        }

        StringBuilder sb = new StringBuilder();
        if (!StringUtils.isNullOrBlank(config.getChangelog().getTitle())) {
            Heading heading = new Heading(
                EmojiUtil.processEmojis(config.getChangelog().getTitle()),
                1
            );
            heading.setUnderlineStyle(false);
            sb.append(heading).append("\n\n");
        }

        if (!StringUtils.isNullOrBlank(subtitle)) {
            Heading headingSubtitle = new Heading(subtitle, 2);
            headingSubtitle.setUnderlineStyle(false);
            sb.append(headingSubtitle).append("\n\n");
        }

        if (config.getChangelog().isShowBreakingChanges()) {
            List<String> allBreakingChanges = new ArrayList<>();

            groupedByType.forEach((type, commitMessages) -> {
                List<CommitMessage> breakingChanges = commitMessages.stream()
                    .filter(CommitMessage::breakingChanges)
                    .toList();

                commitMessages.removeAll(breakingChanges);

                allBreakingChanges.addAll(
                    breakingChanges.stream()
                        .map(message -> message.formatForChangelog(
                            config.getChangelog().getFormat(), ChangelogScope.BREAKING_CHANGES)
                        )
                        .toList()
                );
            });
            if (!allBreakingChanges.isEmpty()) {
                sb.append(new Heading("Breaking Changes", 3)).append("\n\n");
                sb.append(new UnorderedList<>(allBreakingChanges)).append("\n\n");
            }
        }

        types.forEach((typeKey, typeTitle) -> {
            if (groupedByType.containsKey(typeKey)) {
                List<String> messages = groupedByType.get(typeKey).stream()
                    .map(message -> message.formatForChangelog(
                        config.getChangelog().getFormat(),
                        ChangelogScope.SECTION
                    ))
                    .collect(Collectors.toList());
                if (!messages.isEmpty()) {
                    sb.append(new Heading(typeTitle, 3)).append("\n\n");
                    sb.append(new UnorderedList<>(messages)).append("\n\n");
                }

                groupedByType.remove(typeKey);
            }
        });

        if (config.getChangelog().isShowOtherTypes()) {
            List<String> others = groupedByType.values()
                .stream()
                .flatMap(List::stream)
                .map(message -> message.formatForChangelog(
                    config.getChangelog().getFormat(),
                    ChangelogScope.OTHER_TYPES
                ))
                .toList();
            if (!others.isEmpty()) {
                sb.append(new Heading(I18nService.getInstance().getMessage("changelog.other_changes"), 3))
                    .append("\n\n");
                sb.append(new UnorderedList<>(others));
            }
        }

        return sb;
    }

    /**
     * Writes a changelog file based on grouped commit messages and predefined values.
     *
     * @param content the content to be written to the changelog file.
     * @param append  if {@code true}, appends to the existing file; if {@code false}, overwrites it.
     * @return the {@link Path} to the generated changelog file.
     * @throws IOException if there is an error creating or writing to the changelog file.
     */
    public Path writeChangeLog(String content, boolean append) throws IOException {
        Path changelogFile = this.getChangelogFile();

        if (append) {
            String separator = Files.exists(changelogFile) ? "\n\n" : "";
            Files.writeString(changelogFile, separator + content, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } else {
            Files.writeString(changelogFile, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }
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
