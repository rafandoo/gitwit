package dev.rafandoo.gitwit.service.git;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rafandoo.gitwit.App;
import dev.rafandoo.gitwit.enums.GitConfigScope;
import dev.rafandoo.gitwit.enums.GitRepositoryParam;
import dev.rafandoo.gitwit.exception.GitWitException;
import dev.rafandoo.gitwit.service.MessageService;
import lombok.AllArgsConstructor;
import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.util.FS;
import org.eclipse.jgit.util.SystemReader;

import java.io.IOException;
import java.util.Locale;

@Singleton
@AllArgsConstructor(onConstructor_ = @__({@Inject}))
public final class GitConfigService {

    private final GitService gitService;
    private final MessageService messageService;

    public static final String GIT_CONFIG_CORE = "core";
    public static final String GIT_CONFIG_HOOKS_PATH = "hooksPath";

    /**
     * Loads the Git configuration based on the specified configuration scope.
     *
     * @param scope the configuration scope (GLOBAL or LOCAL) to load the Git configuration from.
     * @return a {@link StoredConfig} representing the Git configuration for the specified scope.
     * @throws IOException if there is an error accessing the Git configuration.
     */
    public StoredConfig load(GitConfigScope scope) throws IOException {
        return switch (scope) {
            case GLOBAL -> SystemReader.getInstance().openUserConfig(null, FS.DETECTED);
            case LOCAL -> this.gitService.withGit(git -> git.getRepository().getConfig());
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
            StoredConfig config = this.load(scope);
            config.load();

            String alias = GitRepositoryParam.GITWIT_ALIAS.get().asString();
            String existing = config.getString("alias", null, alias);

            if (add) {
                if (existing == null) {
                    config.setString("alias", null, alias, this.getAliasCommand());
                    config.save();
                    this.messageService.info("git.alias.set", scope.name().toLowerCase(Locale.ROOT));
                } else {
                    this.messageService.info("git.alias.exists", scope.name().toLowerCase(Locale.ROOT));
                }
            } else {
                if (existing != null) {
                    config.unset("alias", null, alias);
                    config.save();
                    this.messageService.info("git.alias.removed", scope.name().toLowerCase(Locale.ROOT));
                } else {
                    this.messageService.info("git.alias.not_configured", scope.name().toLowerCase(Locale.ROOT));
                }
            }
        } catch (IOException e) {
            throw new GitWitException("git.error.init_failed", e);
        } catch (ConfigInvalidException e) {
            throw new GitWitException("git.error.config_invalid", e);
        }
    }

    /**
     * Constructs the command string to be used for the GitWit alias.
     * <p>
     * The command differs based on whether the application is running from a JAR file or not.
     *
     * @return the command string for the GitWit alias.
     */
    public String getAliasCommand() {
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
}
