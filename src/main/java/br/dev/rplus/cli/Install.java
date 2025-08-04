package br.dev.rplus.cli;

import br.dev.rplus.enums.ExceptionMessage;
import br.dev.rplus.enums.GitRepositoryParam;
import br.dev.rplus.exception.GitWitException;
import br.dev.rplus.service.GitService;
import br.dev.rplus.service.MessageService;
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

    @Override
    public void run() {
        if (this.hook && this.global) {
            throw new GitWitException(
                ExceptionMessage.GENERAL,
                MessageService.getInstance()
                    .getErrorMessage("install.conflict.hook_global")
                    .toAnsi()
            );
        }

        if (this.hook) {
            MessageService.getInstance().info("install.hook.start");
            GitService.getInstance().setupCommitWizardHook(this.force);
            MessageService.getInstance().success("install.hook.success");
            return;
        }

        if (this.global) {
            MessageService.getInstance().info("install.alias.global");
            GitService.getInstance().configureGitAliasGlobal();
        } else {
            MessageService.getInstance().info("install.alias.local");
            GitService.getInstance().configureGitAliasLocal();
        }
        MessageService.getInstance().success(
            "install.success",
            GitRepositoryParam.GITWIT_ALIAS.get().asString()
        );
    }
}
