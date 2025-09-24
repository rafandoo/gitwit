package dev.rafandoo.gitwit.service;

import dev.rafandoo.gitwit.App;
import dev.rafandoo.gitwit.entity.CommitMessage;
import dev.rafandoo.gitwit.enums.GitConfigScope;
import dev.rafandoo.gitwit.enums.GitRepositoryParam;
import dev.rafandoo.gitwit.enums.ExceptionMessage;
import dev.rafandoo.gitwit.exception.GitWitException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.util.FS;
import org.eclipse.jgit.util.SystemReader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.List;
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
            this.getAliasCommand().replace("!", "") + " hook $COMMIT_MSG_FILE || {\n" +
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
            config.load();
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
        } catch (ConfigInvalidException e) {
            throw new GitWitException(ExceptionMessage.GIT_CONFIG_INVALID, e);
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
            config.load();
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
            throw new GitWitException(ExceptionMessage.INIT_REPOSITORY_FAILED, e);
        } catch (ConfigInvalidException e) {
            throw new GitWitException(ExceptionMessage.GIT_CONFIG_INVALID, e);
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
            case GLOBAL -> SystemReader.getInstance().openUserConfig(null, FS.DETECTED);
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
            config.load();

            String alias = GitRepositoryParam.GITWIT_ALIAS.get().asString();
            String existing = config.getString("alias", null, alias);

            if (add) {
                if (existing == null) {
                    config.setString("alias", null, alias, this.getAliasCommand());
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
        } catch (ConfigInvalidException e) {
            throw new GitWitException(ExceptionMessage.GIT_CONFIG_INVALID, e);
        }
    }

    /**
     * Constructs the command string to be used for the GitWit alias.
     * <p>
     * The command differs based on whether the application is running from a JAR file or not.
     *
     * @return the command string for the GitWit alias.
     */
    private String getAliasCommand() {
        String command;
        if (App.isRunningFromJar()) {
            command = String.format(
                "!java -jar %s",
                App.getApplicationPath().toString().replace("\\", "/")
            );
        } else {
            command = String.format(
                "!%s",
                App.getApplicationPath().toString().replace("\\", "/")
            );
        }
        return command;
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
     * @param amend         if true, amends the last commit instead of creating a new one.
     * @return the created {@link RevCommit} representing the new commit.
     * @throws GitWitException if any Git-related errors occur during the commit process.
     */
    public RevCommit commit(CommitMessage commitMessage, boolean autoAdd, boolean amend) {
        RevCommit commit;
        try (Git git = Git.open(this.getGit().toFile())) {
            if (autoAdd) {
                MessageService.getInstance().info("git.service.commit.adding_files");
                git.add().addFilepattern(".").setRenormalize(false).call();
            }
            commit = git.commit()
                .setMessage(commitMessage.format())
                .setSign(false)
                .setAllowEmpty(amend)
                .setAmend(amend)
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
     * @return list of {@link RevCommit}, inclusive from and to (if reachable).
     */
    public List<RevCommit> getCommits(String from, String to) {
        try (Git git = Git.open(this.getGit().toFile())) {
            Repository repo = git.getRepository();
            RevWalk walk = new RevWalk(repo);
            walk.setRetainBody(true);

            List<RevCommit> commitList = new ArrayList<>();

            if (from == null && to == null) {
                // Only the latest commit
                ObjectId headId = repo.resolve(Constants.HEAD);
                if (headId != null) {
                    commitList.add(walk.parseCommit(headId));
                }
                return commitList;
            }

            if (from != null && to == null) {
                // All commits reachable from `from`
                ObjectId fromId = this.resolveCommitId(repo, walk, from);
                git.log().add(fromId).call().forEach(commitList::add);
                return commitList;
            }

            if (from == null) {
                // Only the `to` commit
                ObjectId toId = this.resolveCommitId(repo, walk, to);
                commitList.add(walk.parseCommit(toId));
                return commitList;
            }

            // Range between `from` and `to`, inclusive
            ObjectId fromId = this.resolveCommitId(repo, walk, from);
            ObjectId toId = this.resolveCommitId(repo, walk, to);

            // Add intermediate commits (excluding from)
            for (RevCommit commit : git.log().addRange(fromId, toId).call()) {
                commitList.add(commit);
            }

            // Add 'from' explicitly (inclusive)
            if (!this.isTag(repo, walk, from)) {
                commitList.add(walk.parseCommit(fromId));
            }

            return commitList;
        } catch (MissingObjectException e) {
            throw new GitWitException(ExceptionMessage.MISSING_OBJECT, e);
        } catch (IOException e) {
            throw new GitWitException(ExceptionMessage.INIT_REPOSITORY_FAILED, e);
        } catch (NoHeadException e) {
            throw new GitWitException(ExceptionMessage.NO_HEAD);
        } catch (GitAPIException e) {
            throw new GitWitException(ExceptionMessage.GIT_API_EXCEPTION, e);
        }
    }

    /**
     * Resolves a rev-spec (branch, tag, commit hash) to a {@link ObjectId} of a commit.
     * Supports annotated tags by dereferencing them to the commit they point to.
     *
     * @param repo the Git repository.
     * @param walk the {@link RevWalk} instance for parsing commits.
     * @param rev  the rev-spec to resolve.
     * @return the resolved {@link ObjectId} of the commit.
     */
    private ObjectId resolveCommitId(Repository repo, RevWalk walk, String rev) throws IOException {
        ObjectId id = repo.resolve(rev);
        RevObject obj = walk.parseAny(id);

        if (obj instanceof RevTag tag) {
            return tag.getObject();
        } else if (obj instanceof RevCommit commit) {
            return commit.getId();
        } else {
            throw new GitWitException(ExceptionMessage.UNSUPPORTED_OBJECT_TYPE, String.valueOf(obj.getType()));
        }
    }

    /**
     * Checks if the given rev-spec resolves to a tag in the repository.
     *
     * @param repository the Git repository.
     * @param walk       the {@link RevWalk} instance for parsing objects.
     * @param rev        the rev-spec to check.
     * @return {@code true} if the rev-spec is a tag, {@code false} otherwise.
     * @throws IOException if there is an error resolving the rev-spec or parsing the object.
     */
    private boolean isTag(Repository repository, RevWalk walk, String rev) throws IOException {
        ObjectId id = repository.resolve(rev);
        if (id == null) {
            return false;
        }
        RevObject obj = walk.parseAny(id);
        return obj instanceof RevTag;
    }
}
