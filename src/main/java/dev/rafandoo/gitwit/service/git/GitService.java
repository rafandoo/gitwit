package dev.rafandoo.gitwit.service.git;

import com.google.inject.Singleton;
import dev.rafandoo.gitwit.exception.GitWitException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;

import java.io.IOException;
import java.nio.file.*;

/**
 * Class responsible for managing Git operations.
 * <p>
 * This class provides methods to interact with the Git repository, such as retrieving the repository path,
 * and retrieving commits.
 */
@Singleton
public final class GitService {

    /**
     * Returns the path to the Git repository root.
     *
     * @return an absolute {@link Path} to the repository directory.
     */
    public Path getRepo() {
        return Paths.get(System.getProperty("user.dir", "")).toAbsolutePath();
    }

    /**
     * Retrieves the .git directory for the current repository.
     *
     * @return {@link Path} to the .git directory.
     * @throws GitWitException if the .git directory does not exist.
     */
    public Path getGit() {
        Path git = this.getRepo().resolve(Constants.DOT_GIT);
        if (!Files.isDirectory(git)) {
            throw new GitWitException("git.error.not_a_repo");
        }
        return git;
    }

    /**
     * Executes a function with an opened Git repository.
     *
     * @param fn  the function to execute, which takes a {@link Git} instance as input and returns a result of type T.
     * @param <T> the type of the result returned by the function.
     * @return the result of executing the provided function.
     * @throws GitWitException if there is an error initializing the Git repository.
     */
    public <T> T withGit(GitFunction<T> fn) {
        try (
            Git git = Git.open(getGit().toFile());
        ) {
            return fn.apply(git);
        } catch (IOException e) {
            throw new GitWitException("git.error.init_failed", e);
        }
    }
}
