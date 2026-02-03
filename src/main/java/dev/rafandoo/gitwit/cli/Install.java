package dev.rafandoo.gitwit.cli;

import com.google.inject.Inject;
import dev.rafandoo.gitwit.enums.GitRepositoryParam;
import dev.rafandoo.gitwit.exception.GitWitException;
import dev.rafandoo.gitwit.service.GitService;
import picocli.CommandLine;

/**
 * <h2>install</h2>
 * <p>
 * Command that installs or updates GitWit in the current Git repository or globally.
 * <p>
 * You may choose to:
 *  <ul>
 *      <li>Install the interactive commit wizard hook via {@code --hook}</li>
 *      <li>Register a Git alias to use GitWit as a CLI tool via {@code --global} or local configuration</li>
 * </ul>
 */
@CommandLine.Command(
    name = "install",
    resourceBundle = "i18n.commands.install",
    sortOptions = false
)
public class Install extends BaseCommand {

    @CommandLine.Option(
        names = {"-hk", "--hook"},
        descriptionKey = "install.option.hook"
    )
    private boolean hook;

    @CommandLine.Option(
        names = {"-f", "--force"},
        descriptionKey = "install.option.force"
    )
    private boolean force;

    @CommandLine.Option(
        names = {"-g", "--global"},
        descriptionKey = "install.option.global"
    )
    private boolean global;

    @Inject
    private GitService gitService;

    @Override
    public void run() {
        if (this.hook && this.global) {
            throw new GitWitException("install.error.conflict", true);
        }

        if (this.hook) {
            messageService.info("install.hook.start");
            this.gitService.setupCommitWizardHook(this.force);
            messageService.success("install.hook.success");
            return;
        }

        if (this.global) {
            messageService.info("install.alias.global");
            this.gitService.configureGitAliasGlobal();
        } else {
            messageService.info("install.alias.local");
            this.gitService.configureGitAliasLocal();
        }
        messageService.success(
            "install.success",
            GitRepositoryParam.GITWIT_ALIAS.get().asString()
        );
    }
}
