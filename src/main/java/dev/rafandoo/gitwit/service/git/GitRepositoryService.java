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
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Singleton
@AllArgsConstructor(onConstructor_ = @__({@Inject}))
public final class GitRepositoryService {

    private final GitService gitService;
    private final MessageService messageService;

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

    @FunctionalInterface
    private interface RepoFunction<T> {
        T apply(Git git, Repository repo, RevWalk walk);
    }

    /**
     * Returns the list of commits between two references (inclusive).
     *
     * @param from any rev‑spec accepted by Git (tag, branch, hash).
     * @param to   any rev‑spec accepted by Git (tag, branch, hash).
     * @return list of {@link RevCommit}, inclusive from and to (if reachable).
     */
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
    public Optional<RevCommit> resolveCommit(String revSpec) {
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
            if (parts[1] == null || parts[1].isBlank()) {
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

        if (ignoredMessages != null) {
            Pattern ignoredPattern = Pattern.compile(
                ignoredMessages.stream()
                    .map(EmojiUtil::replaceEmojiWithAlias)
                    .collect(Collectors.joining("|"))
            );

            commits.removeIf(commit ->
                ignoredPattern.matcher(EmojiUtil.replaceEmojiWithAlias(commit.getFullMessage())).find()
            );
        }

        return commits;
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
     * @throws IOException if there is an error resolving the rev-spec or parsing the commit.
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

        if (obj instanceof RevTag tag) {
            return tag.getObject();
        } else if (obj instanceof RevCommit commit) {
            return commit.getId();
        } else {
            throw new GitWitException("git.error.unsupported", String.valueOf(obj.getType()));
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

//    public String getLatestTag() {
//        try (
//            Git git = Git.open(this.getGit().toFile());
//            Repository repo = git.getRepository()
//        ) {
//            String tag = git.describe()
//                .setTags(true)
//                .setTarget(repo.resolve(Constants.HEAD))
//                .call();
//
//            if (!StringUtils.isNullOrBlank(tag)) {
//                return tag.replaceAll("-(\\d+)-g[0-9a-f]+$", "");
//            }
//            return null;
//        } catch (IOException | GitAPIException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
