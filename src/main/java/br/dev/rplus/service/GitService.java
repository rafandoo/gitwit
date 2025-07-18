package br.dev.rplus.service;

import br.dev.rplus.App;
import br.dev.rplus.entity.CommitMessage;
import br.dev.rplus.enums.GitConfigScope;
import br.dev.rplus.enums.GitRepositoryParam;
import br.dev.rplus.enums.ExceptionMessage;
import br.dev.rplus.exception.GitWitException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.util.SystemReader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.stream.Stream;

/**
 * Class responsible for managing Git operations.
 * <p>
 * This class provides methods to interact with the Git repository, such as retrieving the repository path,
 * and retrieving commits.
 */
public final class GitService {

    private static GitService instance;

    /**
     * Private constructor to prevent instantiation.
     */
    private GitService() {
    }

    /**
     * Returns the singleton instance, instantiating it on first use.
     *
     * @return {@link GitService} instance.
     */
    public static synchronized GitService getInstance() {
        if (instance == null) {
            instance = new GitService();
        }
        return instance;
    }

    /**
     * Returns the path to the Git repository root.
     *
     * @return an absolute {@link Path} to the repository directory.
     */
    public Path getRepo() {
        return Paths.get("").toAbsolutePath();
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
            throw new GitWitException(ExceptionMessage.NOT_A_GIT_REPOSITORY);
        }
        return git;
    }

    /**
     * Retrieves the hooks directory for the current Git repository.
     *
     * @return {@link Path} to the hooks directory.
     * @throws GitWitException if the hooks directory cannot be created or accessed.
     */
    public Path getGitHooks() {
        Path hooksDir = this.getRepo().resolve(GitRepositoryParam.HOOKS_DIR_NAME.get().asString());

        try {
            if (!Files.exists(hooksDir)) {
                Files.createDirectories(hooksDir);
                MessageService.getInstance().debug("git.service.hooks.created", hooksDir);
            }

            // Migrate existing hooks (skip *.sample)
            Path defaultHooks = this.getGit().resolve(Constants.HOOKS);
            if (Files.isDirectory(defaultHooks)) {
                try (Stream<Path> files = Files.list(defaultHooks)) {
                    files.filter(Files::isRegularFile)
                        .filter(p -> !p.getFileName().toString().endsWith(".sample"))
                        .forEach(file -> this.moveHookFile(file, hooksDir.resolve(file.getFileName())));
                }
            }
            return hooksDir;
        } catch (IOException e) {
            throw new GitWitException(ExceptionMessage.DEFAULT_HOOKS_MOVE_FAILED, e);
        }
    }

    /**
     * Moves a hook file from one location to another, replacing the destination if it already exists.
     *
     * @param src  the source {@link Path} of the file to be moved.
     * @param dest the destination {@link Path} where the file will be moved.
     * @throws GitWitException if an I/O error occurs during file movement.
     */
    private void moveHookFile(Path src, Path dest) {
        try {
            Files.move(src, dest, StandardCopyOption.REPLACE_EXISTING);
            MessageService.getInstance().debug(
                "git.service.hooks.moved",
                src,
                dest
            );
        } catch (IOException e) {
            throw new GitWitException(ExceptionMessage.HOOK_MOVE_FAILED, e, src.toString());
        }
    }

    /**
     * Installs (or updates) the GitWit hook and switches <code>core.hooksPath</code> to
     * {@link GitRepositoryParam#HOOKS_DIR_NAME}. If the hook already exists and the user did not request <code>--force</code>, the
     * method exits early with an informational message.
     *
     * @param forceInstall whether to overwrite an existing hook.
     */
    public void setupCommitWizardHook(boolean forceInstall) {
        Path hookFile = this.getGitHooks().resolve(GitRepositoryParam.PREPARE_COMMIT_MSG.get().asString());

        if (Files.exists(hookFile) && !forceInstall) {
            MessageService.getInstance().info(
                "git.service.hooks.already_exists"
            );
            return;
        }

        this.createPrepareCommitMsgHook(hookFile);
        this.configureGitHooks();
    }

    /**
     * Creates a prepare-commit-msg hook script for the Commit Wizard.
     *
     * @param hookFile the {@link Path} where the hook script will be written.
     * @throws GitWitException if there is an error writing the hook script.
     */
    private void createPrepareCommitMsgHook(Path hookFile) {
        String script = "#!/usr/bin/env bash\n" +
            "# Auto‑generated by GitWit — do not edit manually.\n" +
            "COMMIT_MSG_FILE=\"$1\"\n" +
            "# Ignore merge & squash commits\n" +
            "case \"$2\" in\n" +
            "  merge|squash) exit 0 ;;\n" +
            "esac\n" +
            "# Run the Commit Wizard to obtain the message\n" +
            "java -jar " + App.getApplicationPath().toString().replace("\\", "/") + " hook $COMMIT_MSG_FILE || {\n" +
            "  echo 'Commit Wizard failed; aborting commit.' >&2\n" +
            "  exit 1\n" +
            "}\n";

        try {
            Files.writeString(
                hookFile,
                script,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
            );
            // Make sure the hook is executable (best‑effort on non‑POSIX file systems)
            try {
                Files.setPosixFilePermissions(hookFile, PosixFilePermissions.fromString("rwxr-xr-x"));
            } catch (UnsupportedOperationException ignored) {
                // Non‑POSIX FS (e.g. Windows) – Git will still attempt to execute .sh via sh.exe
            }
            MessageService.getInstance().debug("git.service.hooks.script_written", hookFile.toString());
        } catch (IOException e) {
            throw new GitWitException(ExceptionMessage.PREPARE_HOOK_WRITE_FAILED, e);
        }
    }

    /**
     * Configures the Git hooks directory for the repository.
     * <p>
     * Sets the hooks path to {@link GitRepositoryParam#HOOKS_DIR_NAME} and saves the configuration.
     *
     * @throws GitWitException if there is an error accessing or modifying the Git configuration.
     */
    private void configureGitHooks() {
        try {
            StoredConfig config = this.loadGitConfig(GitConfigScope.LOCAL);
            if (
                !GitRepositoryParam.HOOKS_DIR_NAME.get()
                    .asString()
                    .equals(config.getString("core", null, "hooksPath"))
            ) {
                config.setString(
                    "core",
                    null,
                    "hooksPath",
                    GitRepositoryParam.HOOKS_DIR_NAME.get().asString()
                );

                config.setString(
                    "core",
                    null,
                    "editor",
                    GitRepositoryParam.CORE_EDITOR_CAT.get().asString()
                );

                config.save();
                MessageService.getInstance().info(
                    "git.service.hooks.path_set",
                    GitRepositoryParam.HOOKS_DIR_NAME.get().asString()
                );
                MessageService.getInstance().info(
                    "git.service.hooks.editor_set",
                    GitRepositoryParam.CORE_EDITOR_CAT.get().asString()
                );
            }
        } catch (IOException e) {
            throw new GitWitException(ExceptionMessage.CORE_HOOK_PATH_FAILED, e);
        }
    }

    /**
     * Uninstalls the GitWit commit wizard hook from the local Git repository.
     * <p>
     * This method performs the following actions:
     * <ul>
     *   <li>Deletes the prepare-commit-msg hook file if it exists</li>
     *   <li>Removes the custom hooks path and editor configuration from the local Git config</li>
     * </ul>
     *
     * @throws GitWitException if an error occurs during hook removal or configuration modification.
     */
    public void uninstallCommitWizardHook() {
        Path hookFile = this.getGitHooks().resolve(GitRepositoryParam.PREPARE_COMMIT_MSG.get().asString());
        try {
            if (Files.exists(hookFile)) {
                Files.delete(hookFile);
                MessageService.getInstance().info("git.service.hooks.removed", hookFile);
            } else {
                MessageService.getInstance().info("git.service.hooks.none_to_remove");
            }

            StoredConfig config = this.loadGitConfig(GitConfigScope.LOCAL);
            String configuredHooksPath = config.getString("core", null, "hooksPath");

            if (GitRepositoryParam.HOOKS_DIR_NAME.get().asString().equals(configuredHooksPath)) {
                config.unset("core", null, "hooksPath");
                config.unset("core", null, "editor");
                config.save();
                MessageService.getInstance().info("git.service.hooks.config_cleared");
            } else {
                MessageService.getInstance().info("git.service.hooks.not_configured");
            }
        } catch (IOException e) {
            // TODO: Handle this exception more gracefully
            throw new GitWitException(ExceptionMessage.GENERAL, e);
        }
    }

    /**
     * Checks if the Commit Wizard hook is currently enabled in the Git repository.
     *
     * @return {@code true} if the prepare-commit-msg hook exists, {@code false} otherwise.
     */
    public boolean isCommitWizardHookEnabled() {
        return Files.exists(this.getGitHooks().resolve(GitRepositoryParam.PREPARE_COMMIT_MSG.get().asString()));
    }

    /**
     * Loads the Git configuration based on the specified configuration scope.
     *
     * @param scope the configuration scope (GLOBAL or LOCAL) to load the Git configuration from.
     * @return a {@link StoredConfig} representing the Git configuration for the specified scope.
     * @throws IOException if there is an error accessing the Git configuration.
     */
    private StoredConfig loadGitConfig(GitConfigScope scope) throws IOException {
        return switch (scope) {
            case GLOBAL -> SystemReader.getInstance().openUserConfig(null, null);
            case LOCAL -> {
                try (Git git = Git.open(this.getGit().toFile())) {
                    yield git.getRepository().getConfig();
                }
            }
        };
    }

    /**
     * Handles the configuration or removal of a GitWit alias in the specified Git configuration scope.
     *
     * @param scope the configuration scope (GLOBAL or LOCAL) to modify the alias.
     * @param add   {@code true} to add the alias, {@code false} to remove it.
     * @throws GitWitException if there is an error accessing or modifying the Git configuration.
     */
    private void handleAlias(GitConfigScope scope, boolean add) {
        try {
            StoredConfig config = this.loadGitConfig(scope);

            String alias = GitRepositoryParam.GITWIT_ALIAS.get().asString();
            String existing = config.getString("alias", null, alias);

            if (add) {
                if (existing == null) {
                    // TODO: Create a new alias for each scope
                    String command = String.format(
                        "!java -jar %s",
                        App.getApplicationPath().toString().replace("\\", "/")
                    );

                    config.setString("alias", null, alias, command);
                    config.save();
                    MessageService.getInstance().info("git.service.alias.configured", scope.name().toLowerCase());
                } else {
                    MessageService.getInstance().info("git.service.alias.already_configured", scope.name().toLowerCase());
                }
            } else {
                if (existing != null) {
                    config.unset("alias", null, alias);
                    config.save();
                    MessageService.getInstance().info("git.service.alias.removed", scope.name().toLowerCase());
                } else {
                    MessageService.getInstance().info("git.service.alias.not_configured", scope.name().toLowerCase());
                }
            }
        } catch (IOException e) {
            throw new GitWitException(ExceptionMessage.INIT_REPOSITORY_FAILED, e);
        }
    }

    /**
     * Configures the GitWit alias globally in the user's Git configuration.
     *
     * @throws GitWitException if there is an error accessing or modifying the global Git configuration.
     */
    public void configureGitAliasGlobal() {
        this.handleAlias(GitConfigScope.GLOBAL, true);
    }

    /**
     * Removes the GitWit alias from the global Git configuration.
     *
     * @throws GitWitException if there is an error accessing or modifying the global Git configuration.
     */
    public void removeGitAliasGlobal() {
        this.handleAlias(GitConfigScope.GLOBAL, false);
    }

    /**
     * Configures the GitWit alias locally in the current Git repository's configuration.
     *
     * @throws GitWitException if there is an error accessing or modifying the local Git configuration.
     */
    public void configureGitAliasLocal() {
        this.handleAlias(GitConfigScope.LOCAL, true);
    }

    /**
     * Removes the GitWit alias from the local Git repository's configuration.
     *
     * @throws GitWitException if there is an error accessing or modifying the local Git configuration.
     */
    public void removeGitAliasLocal() {
        this.handleAlias(GitConfigScope.LOCAL, false);
    }

    /**
     * Commits changes to the Git repository with an optional automatic file staging.
     *
     * @param commitMessage the commit message to be used for the commit.
     * @param autoAdd       if true, automatically stages all files in the repository before committing.
     * @return the created {@link RevCommit} representing the new commit.
     * @throws GitWitException if any Git-related errors occur during the commit process.
     */
    public RevCommit commit(CommitMessage commitMessage, boolean autoAdd) {
        RevCommit commit;
        try (Git git = Git.open(this.getGit().toFile())) {
            if (autoAdd) {
                git.add().addFilepattern(".").call();
                MessageService.getInstance().info("git.service.commit.adding_files");
            }
            commit = git.commit()
                .setMessage(commitMessage.format())
                .setSign(false)
                .setAllowEmpty(false)
                .call();
        } catch (IOException e) {
            throw new GitWitException(ExceptionMessage.INIT_REPOSITORY_FAILED, e);
        } catch (NoHeadException e) {
            throw new GitWitException(ExceptionMessage.NO_HEAD);
        } catch (UnmergedPathsException e) {
            throw new GitWitException(ExceptionMessage.UNMERGED_PATHS);
        } catch (WrongRepositoryStateException e) {
            throw new GitWitException(ExceptionMessage.WRONG_REPOSITORY_STATE, e);
        } catch (ServiceUnavailableException e) {
            throw new GitWitException(ExceptionMessage.SERVICE_UNAVAILABLE, e);
        } catch (ConcurrentRefUpdateException e) {
            throw new GitWitException(ExceptionMessage.CONCURRENT_REF_UPDATE, e);
        } catch (AbortedByHookException e) {
            throw new GitWitException(ExceptionMessage.ABORTED_BY_HOOK, e);
        } catch (NoMessageException e) {
            throw new GitWitException(ExceptionMessage.NO_COMMIT_MESSAGE);
        } catch (EmptyCommitException e) {
            throw new GitWitException(ExceptionMessage.EMPTY_COMMIT);
        } catch (GitAPIException e) {
            throw new GitWitException(ExceptionMessage.GIT_API_EXCEPTION, e);
        }
        return commit;
    }

    /**
     * Returns the list of commits between two references (inclusive).
     *
     * @param from any rev‑spec accepted by Git (tag, branch, hash).
     * @param to   any rev‑spec accepted by Git (tag, branch, hash).
     * @return iterable of {@link RevCommit}.
     */
    public Iterable<RevCommit> getCommits(String from, String to) {
        try (Git git = Git.open(this.getGit().toFile())) {
            Repository repo = git.getRepository();

            if (from == null) {
                return git.log().setMaxCount(1).call();
            }

            if (to == null) {
                return git.log().add(repo.resolve(from)).call();
            }

            return git.log().add(repo.resolve(from)).add(repo.resolve(to)).call();
        } catch (IOException | GitAPIException e) {
            throw new RuntimeException(e);
        }
    }
}
