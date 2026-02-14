package dev.rafandoo.gitwit.service.git;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rafandoo.cup.utils.StringUtils;
import dev.rafandoo.gitwit.exception.GitWitException;
import dev.rafandoo.gitwit.service.MessageService;
import dev.rafandoo.gitwit.util.EmojiUtil;
import lombok.AllArgsConstructor;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Service for interacting with the Git repository using JGit.
 * <p>
 * Provides methods to list commits between references, resolve
 * rev-specs to commits, and handle commit filtering based on message patterns.
 */
@Singleton
@AllArgsConstructor(onConstructor_ = @__({@Inject}))
public final class GitRepositoryService {

    private final GitService gitService;
    private final MessageService messageService;

    private static final Pattern DESCRIBE_SUFFIX = Pattern.compile("-(\\d+)-g[0-9a-f]+$");

    /**
     * Executes a function with an opened Git repository.
     *
     * @param fn  the function to execute, which takes a {@link Git} instance, a {@link Repository}, and a {@link RevWalk} as input and returns a result of type T.
     * @param <T> the type of the result returned by the function.
     * @return the result of executing the provided function.
     */
    private <T> T withRepo(RepoFunction<T> fn) {
        return this.gitService.withGit(git -> {
            try (
                Repository repo = git.getRepository();
                RevWalk walk = new RevWalk(repo)
            ) {
                return fn.apply(git, repo, walk);
            }
        });
    }

    /**
     * Returns the list of commits between two references (inclusive).
     *
     * @param from any rev‑spec accepted by Git (tag, branch, hash).
     * @param to   any rev‑spec accepted by Git (tag, branch, hash).
     * @return list of {@link RevCommit}, inclusive from and to (if reachable).
     */
    // TODO: private
    public List<RevCommit> listCommitsBetween(String from, String to) {
        return this.withRepo((git, repo, walk) -> {
            try {
                walk.setRetainBody(true);
                List<RevCommit> commits = new ArrayList<>();

                // Range between `from` and `to`, inclusive
                ObjectId fromId = this.resolveCommitId(repo, walk, from);
                ObjectId toId = this.resolveCommitId(repo, walk, to);

                // Add intermediate commits (excluding from)
                for (RevCommit commit : git.log().addRange(fromId, toId).call()) {
                    commits.add(commit);
                }

                // Add 'from' explicitly (inclusive)
                if (!this.isTag(repo, walk, from)) {
                    commits.add(walk.parseCommit(fromId));
                }

                return commits;
            } catch (MissingObjectException e) {
                throw new GitWitException("git.repo.error.missing_object", e);
            } catch (IOException e) {
                throw new GitWitException("git.error.init_failed", e);
            } catch (NoHeadException e) {
                throw new GitWitException("git.repo.error.no_head");
            } catch (GitAPIException e) {
                throw new GitWitException("git.error.api_exception", e);
            }
        });
    }

    /**
     * Resolves a rev-spec (branch, tag, commit hash) to a {@link RevCommit}.
     *
     * @param revSpec the rev-spec to resolve.
     * @return an {@link Optional} containing the resolved {@link RevCommit}, or empty if the rev-spec is null or blank.
     * @throws GitWitException if there is an error resolving the rev-spec or parsing the commit.
     */
    private Optional<RevCommit> resolveCommit(String revSpec) {
        if (StringUtils.isNullOrBlank(revSpec)) {
            return Optional.empty();
        }

        return this.gitService.withGit(git -> {
            try (
                RevWalk walk = new RevWalk(git.getRepository())
            ) {
                ObjectId id = resolveCommitId(git.getRepository(), walk, revSpec);
                return Optional.ofNullable(walk.parseCommit(id));
            } catch (MissingObjectException e) {
                throw new GitWitException("git.repo.error.missing_object", e);
            } catch (IOException e) {
                throw new GitWitException("git.error.init_failed", e);
            }
        });
    }

    /**
     * Resolves a rev-spec to a list of commits. If the rev-spec contains a range (e.g., "HEAD~5..HEAD"), it returns all commits in that range.
     * Otherwise, it resolves the rev-spec to a single commit.
     *
     * @param revSpec the rev-spec to resolve.
     * @return a list of {@link RevCommit} objects corresponding to the resolved rev-spec.
     * @throws GitWitException if there is an error resolving the rev-spec or parsing the commits.
     */
    public List<RevCommit> resolveCommits(String revSpec) {
        if (StringUtils.isNullOrBlank(revSpec)) {
            throw new GitWitException("git.repo.error.rev_not_found", revSpec);
        }

        if (revSpec.contains("..")) {
            String[] parts = revSpec.split("\\.\\.", 2);
            if (StringUtils.isNullOrBlank(parts[1])) {
                parts[1] = Constants.HEAD;
            }
            return this.listCommitsBetween(parts[0], parts[1]);
        } else {
            return this.resolveCommit(revSpec).stream().collect(Collectors.toList());
        }
    }

    /**
     * Resolves the list of Git commits based on the provided revision specification or range.
     *
     * @param revSpec         the Git revision specification (e.g., commit hash, tag, branch).
     * @param from            the starting point of the commit range (deprecated, use revSpec instead).
     * @param to              the ending point of the commit range (deprecated, use revSpec instead).
     * @param ignoredMessages a list of commit message patterns to ignore (optional).
     * @return a list of resolved {@link RevCommit} objects.
     */
    public List<RevCommit> resolveCommits(String revSpec, String from, String to, List<String> ignoredMessages) {
        List<RevCommit> commits;

        if (!StringUtils.isNullOrBlank(revSpec)) {
            commits = this.resolveCommits(revSpec);
        } else if (!StringUtils.isNullOrBlank(from) || !StringUtils.isNullOrBlank(to)) {
            this.messageService.warn("warn.deprecated-range-options");
            String range = String.format(
                "%s..%s",
                StringUtils.isNullOrBlank(from) ? Constants.HEAD : from,
                StringUtils.isNullOrBlank(to) ? Constants.HEAD : to
            );
            commits = this.resolveCommits(range);
        } else {
            commits = this.resolveCommit(Constants.HEAD).stream().collect(Collectors.toList());
        }

        return this.filterIgnored(commits, ignoredMessages);
    }

    /**
     * Filters out commits whose messages match any of the provided ignored message patterns.
     *
     * @param commits         the list of commits to filter.
     * @param ignoredMessages a list of commit message patterns to ignore. Each pattern is treated as a regular expression.
     * @return a list of commits whose messages do not match any of the ignored patterns.
     */
    private List<RevCommit> filterIgnored(List<RevCommit> commits, List<String> ignoredMessages) {
        if (ignoredMessages == null || ignoredMessages.isEmpty()) {
            return commits;
        }

        String joined = ignoredMessages.stream()
            .map(EmojiUtil::replaceEmojiWithAlias)
            .collect(Collectors.joining("|"));

        Pattern ignoredPattern = Pattern.compile(joined);

        return commits.stream()
            .filter(commit ->
                !ignoredPattern.matcher(EmojiUtil.replaceEmojiWithAlias(commit.getFullMessage()))
                    .find())
            .collect(Collectors.toList());
    }

    /**
     * Retrieves the latest Git tag in the repository.
     *
     * @return the name of the latest tag in the repository, or {@code null} if no tags are found.
     * @throws GitWitException if there is an error retrieving the tags or parsing the associated commits.
     */
    public String getLatestTag() {
        return this.withRepo(((git, repo, walk) -> this.listTagCommits(git, walk).stream()
            .findFirst()
            .map(TagCommit::name)
            .orElse(null)));
    }

    /**
     * Retrieves the previous Git tag that is reachable from the specified reference.
     *
     * @param from the rev-spec (e.g., commit hash, tag, branch) to start from when searching for the previous tag.
     * @return the name of the previous tag reachable from the specified reference, or {@code null} if no such tag exists.
     * @throws GitWitException if there is an error resolving the reference or retrieving the tags.
     */
    public String getPreviousTag(String from) {
        return this.withRepo(((git, repo, walk) -> {
            try {
                ObjectId fromId = this.resolveCommitId(repo, walk, from);
                int fromCommitTime = walk.parseCommit(fromId).getCommitTime();

                return this.listTagCommits(git, walk).stream()
                    .filter(tag -> tag.commitTime() < fromCommitTime)
                    .findFirst()
                    .map(TagCommit::name)
                    .orElse(null);
            } catch (IOException e) {
                throw new GitWitException("git.error.init_failed", e);
            }
        }));
    }

    /**
     * Retrieves a list of Git tags in the repository, along with their associated
     * commit times, sorted by commit time in descending order.
     *
     * @param git  the Git instance to use for repository operations.
     * @param walk the RevWalk instance for parsing commits and tags.
     * @return a list of TagCommit records containing the normalized tag names and their commit times, sorted by commit time (newest first).
     * @throws GitWitException if there is an error retrieving the tags or parsing the associated commits.
     */
    private List<TagCommit> listTagCommits(Git git, RevWalk walk) {
        try {
            return git.tagList()
                .call()
                .stream()
                .map(ref -> resolveTag(ref, walk))
                .sorted(Comparator
                    .comparing(TagCommit::commitTime)
                    .reversed()
                )
                .toList();
        } catch (GitAPIException e) {
            throw new GitWitException("git.error.api_exception", e);
        }
    }

    /**
     * Checks if the given rev-spec resolves to a tag in the repository.
     *
     * @param repo    the Git repository.
     * @param walk    the {@link RevWalk} instance for parsing objects.
     * @param revSpec the rev-spec to check.
     * @return {@code true} if the rev-spec is a tag, {@code false} otherwise.
     * @throws IOException if there is an error resolving the rev-spec or parsing the object.
     */
    private boolean isTag(Repository repo, RevWalk walk, String revSpec) throws IOException {
        ObjectId id = repo.resolve(revSpec);
        if (id == null) {
            return false;
        }
        RevObject obj = walk.parseAny(id);
        return obj instanceof RevTag;
    }

    /**
     * Resolves a Git tag reference to a {@link TagCommit} containing the tag
     * name and its associated commit time.
     *
     * @param ref  the Git reference representing the tag.
     * @param walk the {@link RevWalk} instance for parsing objects.
     * @return a {@link TagCommit} containing the normalized tag name and its commit time.
     * @throws GitWitException if there is an error resolving the tag or parsing the associated commit.
     */
    private TagCommit resolveTag(Ref ref, RevWalk walk) {
        try {
            RevObject obj = walk.parseAny(ref.getObjectId());

            RevCommit commit = switch (obj) {
                case RevTag tag -> walk.parseCommit(tag.getObject());
                case RevCommit c -> c;
                default -> throw new GitWitException(
                    "git.error.unsupported",
                    String.valueOf(obj.getType())
                );
            };

            String tagName = Repository.shortenRefName(ref.getName());
            return new TagCommit(this.normalizeTag(tagName), commit.getCommitTime());
        } catch (IOException e) {
            throw new GitWitException("git.error.init_failed", e);
        }
    }

    /**
     * Normalizes a tag name by removing any describe suffix (e.g., "-1-gabcdef") that
     * may be present in tags created by "git describe".
     *
     * @param tag the original tag name to normalize.
     * @return the normalized tag name with any describe suffix removed.
     */
    private String normalizeTag(String tag) {
        return DESCRIBE_SUFFIX.matcher(tag).replaceAll("");
    }

    /**
     * Resolves a rev-spec (branch, tag, commit hash) to a {@link ObjectId} of a commit.
     * Supports annotated tags by dereferencing them to the commit they point to.
     *
     * @param repo    the Git repository.
     * @param walk    the {@link RevWalk} instance for parsing commits.
     * @param revSpec the rev-spec to resolve.
     * @return the resolved {@link ObjectId} of the commit.
     * @throws GitWitException if the rev-spec is null, blank, cannot be resolved, or does not point to a commit or tag.
     * @throws IOException     if there is an error resolving the rev-spec or parsing the commit.
     */
    private ObjectId resolveCommitId(Repository repo, RevWalk walk, String revSpec) throws IOException {
        if (StringUtils.isNullOrBlank(revSpec)) {
            throw new GitWitException("git.repo.error.invalid_object", revSpec);
        }
        ObjectId id = repo.resolve(revSpec);
        if (id == null) {
            throw new GitWitException("git.repo.error.rev_not_found", revSpec);
        }
        RevObject obj = walk.parseAny(id);

        return switch (obj) {
            case RevTag tag -> tag.getObject();
            case RevCommit commit -> commit.getId();
            default -> throw new GitWitException(
                "git.error.unsupported",
                String.valueOf(obj.getType())
            );
        };
    }

    /**
     * A functional interface representing a function that takes a Git instance, a Repository, and a RevWalk, and returns a result of type T.
     *
     * @param <T> the type of the result produced by the function.
     */
    @FunctionalInterface
    private interface RepoFunction<T> {

        /**
         * Applies this function to the given Git instance, Repository, and RevWalk.
         *
         * @param git  the Git instance to use for repository operations.
         * @param repo the Repository instance representing the Git repository.
         * @param walk the RevWalk instance for parsing commits and tags.
         * @return the result of applying this function.
         */
        T apply(Git git, Repository repo, RevWalk walk);
    }

    /**
     * A record representing a Git tag and its associated commit time.
     *
     * @param name       the name of the tag.
     * @param commitTime the commit time of the tag, represented as a Unix timestamp (seconds since epoch).
     */
    private record TagCommit(String name, int commitTime) {
    }
}
