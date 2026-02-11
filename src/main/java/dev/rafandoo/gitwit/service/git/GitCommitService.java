package dev.rafandoo.gitwit.service.git;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rafandoo.gitwit.entity.CommitMessage;
import dev.rafandoo.gitwit.exception.GitWitException;
import dev.rafandoo.gitwit.service.MessageService;
import lombok.AllArgsConstructor;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.revwalk.RevCommit;

@Singleton
@AllArgsConstructor(onConstructor_ = @__({@Inject}))
public final class GitCommitService {

    private final GitService gitService;
    private final MessageService messageService;

    /**
     * Commits changes to the Git repository with an optional automatic file staging.
     *
     * @param commitMessage the commit message to be used for the commit.
     * @param autoAdd       if true, automatically stages all files in the repository before committing.
     * @param amend         if true, amends the last commit instead of creating a new one.
     * @param allowEmpty    if true, allows creating an empty commit.
     * @return the created {@link RevCommit} representing the new commit.
     * @throws GitWitException if any Git-related errors occur during the commit process.
     */
    public RevCommit commit(CommitMessage commitMessage, boolean autoAdd, boolean amend, boolean allowEmpty) {
        return this.gitService.withGit(git -> {
            try {
                if (autoAdd) {
                    this.messageService.info("git.commit.adding_files");
                    git.add().addFilepattern(".").setRenormalize(false).call();
                }

                if (commitMessage == null) {
                    throw new GitWitException("git.commit.error.no_message");
                }

                return git.commit()
                    .setMessage(commitMessage.format())
                    .setSign(false)
                    .setAllowEmpty(amend || allowEmpty)
                    .setAmend(amend)
                    .call();
            } catch (NoHeadException e) {
                throw new GitWitException("git.repo.error.no_head");
            } catch (UnmergedPathsException e) {
                throw new GitWitException("git.repo.error.unmerged");
            } catch (WrongRepositoryStateException e) {
                throw new GitWitException("git.repo.error.invalid_state", e);
            } catch (ServiceUnavailableException e) {
                throw new GitWitException("git.error.unavailable", e);
            } catch (ConcurrentRefUpdateException e) {
                throw new GitWitException("git.repo.error.concurrent_update", e);
            } catch (AbortedByHookException e) {
                throw new GitWitException("git.repo.error.aborted_by_hook", e);
            } catch (NoMessageException e) {
                throw new GitWitException("git.commit.error.no_message");
            } catch (EmptyCommitException e) {
                throw new GitWitException("git.commit.error.empty");
            } catch (GitAPIException e) {
                throw new GitWitException("git.error.api_exception", e);
            }
        });
    }
}
