package dev.rafandoo.gitwit.service.changelog;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rafandoo.gitwit.cli.dto.ChangelogOptions;
import dev.rafandoo.gitwit.config.GitWitConfig;
import dev.rafandoo.cup.utils.StringUtils;
import dev.rafandoo.gitwit.entity.Changelog;
import dev.rafandoo.gitwit.entity.CommitMessage;
import dev.rafandoo.gitwit.enums.ChangelogScope;
import dev.rafandoo.gitwit.exception.GitWitException;
import dev.rafandoo.gitwit.service.git.GitRepositoryService;
import dev.rafandoo.gitwit.service.MessageService;
import dev.rafandoo.gitwit.service.changelog.render.Renderer;
import dev.rafandoo.gitwit.util.EmojiUtil;
import lombok.AllArgsConstructor;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service responsible for generating and managing changelogs based on Git commit history.
 * <p>
 * This singleton service provides methods to create changelogs by processing commit messages,
 * grouping them by type, and generating a Markdown-formatted changelog file.
 */
@Singleton
@AllArgsConstructor(onConstructor = @__({@Inject}))
public final class ChangelogService {

    private final MessageService messageService;
    private final GitRepositoryService gitRepositoryService;
    private final Renderer renderer;
    private final ChangelogOutputService outputService;
    private final ChangelogVersionResolver versionResolver;

    /**
     * Handles the generation of a changelog based on the provided revision specification or range.
     *
     * @param revSpec the Git revision specification (e.g., commit hash, tag, branch).
     * @param options the options for generating the changelog, including subtitle, copy to clipboard, and append mode.
     * @param config  the GitWit configuration containing changelog settings.
     */
    public void handle(
        String revSpec,
        ChangelogOptions options,
        GitWitConfig config
    ) {
        List<RevCommit> commits = this.resolveCommits(revSpec, options, config);
        this.messageService.debug("changelog.resolved_commits", commits.size());

        Map<String, List<CommitMessage>> grouped = this.toCommitMessages(commits);
        Map<String, String> types = this.resolveTypes(config);

        String subtitle;
        if (options.getSubtitleOptions().isNoSubtitle()) {
            subtitle = null;
        } else {
            subtitle = this.versionResolver.resolveSubtitle(options);
        }

        this.messageService.debug("changelog.resolved_subtitle", subtitle);

        Changelog changelog = this.generate(config, grouped, types, subtitle, options.getOutputOptions().isStdout());
        if (changelog == null) {
            return;
        }

        String output = this.renderer.render(changelog, options.isAppend());
        this.outputService.output(
            output,
            options.getOutputOptions().isCopyToClipboard(),
            options.isAppend(),
            config,
            options.getOutputOptions().isStdout()
        );

        if (!options.getOutputOptions().isStdout()) {
            this.messageService.success("changelog.generated");
        }
    }

    /**
     * Resolves the list of Git commits based on the provided revision specification and options.
     *
     * @param revSpec the Git revision specification (e.g., commit hash, tag, branch).
     * @param options the options for generating the changelog, including tag and version options.
     * @param config  the GitWit configuration containing changelog settings.
     * @return a list of {@link RevCommit} objects representing the resolved commits.
     */
    private List<RevCommit> resolveCommits(String revSpec, ChangelogOptions options, GitWitConfig config) {
        if (
            options.getTagOptions().isLastTag() ||
                options.getVersionOptions().isMajor() ||
                options.getVersionOptions().isMinor() ||
                options.getVersionOptions().isPatch()
        ) {
            String latestTag = this.gitRepositoryService.getLatestTag();
            return this.gitRepositoryService.resolveCommits(
                String.format("%s..%s", latestTag, Constants.HEAD),
                null,
                null,
                config.getChangelog().getIgnored()
            );
        }

        if (!StringUtils.isNullOrBlank(options.getTagOptions().getForTag())) {
            String forTag = options.getTagOptions().getForTag();
            String previousTag = this.gitRepositoryService.getPreviousTag(forTag);
            if (StringUtils.isNullOrBlank(previousTag)) {
                this.messageService.warn("changelog.warn.no_previous_tag", forTag);
                previousTag = forTag + "^";
            }
            return this.gitRepositoryService.resolveCommits(
                String.format("%s..%s", previousTag, forTag),
                null,
                null,
                config.getChangelog().getIgnored()
            );
        }

        return this.gitRepositoryService.resolveCommits(
            revSpec,
            options.getFrom(),
            options.getTo(),
            config.getChangelog().getIgnored()
        );
    }

    /**
     * Generates a changelog based on the grouped commit messages and configuration.
     *
     * @param config        the GitWit configuration containing changelog settings.
     * @param groupedByType a map of commit messages grouped by their types.
     * @param types         a map defining the types of commits to include in the changelog.
     * @param subtitle      an optional subtitle for the changelog.
     * @param stdout       a boolean indicating whether the output is intended for standard output (console) or not.
     * @return the generated {@link Changelog} object, or {@code null} if no commits are available.
     */
    public Changelog generate(GitWitConfig config, Map<String, List<CommitMessage>> groupedByType, Map<String, String> types, String subtitle, boolean stdout) {
        if (groupedByType.isEmpty()) {
            if (!stdout) {
                this.messageService.warn("changelog.warn.no_commits");
            }
            return null;
        }

        List<String> breakingChanges = this.extractBreakingChanges(config, groupedByType);
        Map<String, List<String>> sections = this.buildSections(config, groupedByType, types);
        List<String> otherTypes = this.extractOtherTypes(config, groupedByType);

        return new Changelog(
            config.getChangelog().getTitle(),
            subtitle,
            breakingChanges,
            sections,
            otherTypes
        );
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
            throw new GitWitException("changelog.error.no_template");
        }
        return template;
    }

    /**
     * Converts a list of Git commits into a map of commit messages grouped by their types.
     *
     * @param commits the list of {@link RevCommit} objects to convert.
     * @return a map where the keys are commit types and the values are lists of {@link CommitMessage} objects.
     */
    private Map<String, List<CommitMessage>> toCommitMessages(List<RevCommit> commits) {
        return commits.stream()
            .map(CommitMessage::of)
            .filter(commitMessage -> {
                if (commitMessage.type() == null) {
                    this.messageService.warn(
                        "changelog.warn.commit_no_type",
                        commitMessage.hash().abbreviate(Constants.OBJECT_ID_ABBREV_STRING_LENGTH).name()
                    );
                    return false;
                }
                return true;
            })
            .collect(Collectors.groupingBy(CommitMessage::type));
    }

    /**
     * Resolves the commit types defined in the changelog configuration.
     *
     * @param config the GitWit configuration containing changelog settings.
     * @return a map where the keys are commit type identifiers and the values are their corresponding titles.
     * @throws GitWitException if no commit types are defined in the configuration.
     */
    private Map<String, String> resolveTypes(GitWitConfig config) {
        Map<String, String> types = config.getChangelog()
            .getTypes()
            .entrySet()
            .stream()
            .collect(Collectors.toMap(
                entry -> EmojiUtil.replaceEmojiWithAlias(entry.getKey()),
                Map.Entry::getValue,
                (a, b) -> a,
                LinkedHashMap::new
            ));

        if (types.isEmpty()) {
            throw new GitWitException("changelog.error.types_required");
        }
        return types;
    }

    /**
     * Extracts breaking changes from the grouped commit messages.
     *
     * @param config        the GitWit configuration containing changelog settings.
     * @param groupedByType a map of commit messages grouped by their types.
     * @return a list of formatted breaking change messages.
     */
    private List<String> extractBreakingChanges(GitWitConfig config, Map<String, List<CommitMessage>> groupedByType) {
        if (!config.getChangelog().isShowBreakingChanges()) {
            return Collections.emptyList();
        }

        List<String> result = new ArrayList<>();

        groupedByType.values()
            .forEach(commits -> {
                List<CommitMessage> breaking = commits.stream()
                    .filter(CommitMessage::breakingChanges)
                    .toList();

                commits.removeAll(breaking);

                breaking.forEach(commitMessage -> result.add(
                    commitMessage.formatForChangelog(
                        this.getChangelogCommitTemplateByScope(
                            config.getChangelog().getFormat(),
                            ChangelogScope.BREAKING_CHANGES
                        )
                    )
                ));
            });

        return result;
    }

    /**
     * Builds the sections of the changelog based on the grouped commit messages and defined types.
     *
     * @param config        the GitWit configuration containing changelog settings.
     * @param groupedByType a map of commit messages grouped by their types.
     * @param types         a map defining the types of commits to include in the changelog.
     * @return a map where the keys are section titles and the values are lists of formatted commit messages.
     */
    private Map<String, List<String>> buildSections(GitWitConfig config, Map<String, List<CommitMessage>> groupedByType, Map<String, String> types) {
        Map<String, List<String>> sections = new LinkedHashMap<>();

        types.forEach((typeKey, title) -> {
            if (!groupedByType.containsKey(typeKey)) {
                return;
            }

            List<String> messages = groupedByType.get(typeKey)
                .stream()
                .map(
                    commitMessage -> commitMessage.formatForChangelog(
                        this.getChangelogCommitTemplateByScope(
                            config.getChangelog().getFormat(),
                            ChangelogScope.SECTION
                        )
                    )
                )
                .toList();

            if (!messages.isEmpty()) {
                sections.put(title, messages);
            }

            groupedByType.remove(typeKey);
        });

        return sections;
    }

    /**
     * Extracts commit messages of types not explicitly defined in the changelog configuration.
     *
     * @param config        the GitWit configuration containing changelog settings.
     * @param groupedByType a map of commit messages grouped by their types.
     * @return a list of formatted commit messages for other types.
     */
    private List<String> extractOtherTypes(GitWitConfig config, Map<String, List<CommitMessage>> groupedByType) {
        if (!config.getChangelog().isShowOtherTypes()) {
            return Collections.emptyList();
        }

        return groupedByType.values()
            .stream()
            .flatMap(List::stream)
            .map(commitMessage -> commitMessage.formatForChangelog(
                this.getChangelogCommitTemplateByScope(
                    config.getChangelog().getFormat(),
                    ChangelogScope.OTHER_TYPES
                )
            ))
            .toList();
    }
}
