package dev.rafandoo.gitwit.cli;

import dev.rafandoo.gitwit.enums.ExceptionMessage;
import dev.rafandoo.gitwit.exception.GitWitException;
import dev.rafandoo.gitwit.service.GitService;
import dev.rafandoo.gitwit.service.MessageService;
import picocli.CommandLine;

/**
 * <h2>uninstall</h2>
 * <p>
 * Command that uninstall the GitWit automation for the current repository.
 */
@CommandLine.Command(
    name = "uninstall",
    resourceBundle = "i18n.commands.uninstall",
    sortOptions = false
)
public class Uninstall extends BaseCommand {

    @CommandLine.Option(
        names = {"-hk", "--hook"},
        descriptionKey = "uninstall.option.hook"
    )
    private boolean hook;

    @CommandLine.Option(
        names = {"-g", "--global"},
        descriptionKey = "uninstall.option.global"
    )
    private boolean global;

    @Override
    public void run() {
        if (this.hook && this.global) {
            throw new GitWitException(
                ExceptionMessage.GENERAL,
                MessageService.getInstance()
                    .getErrorMessage("uninstall.conflict.hook_global")
                    .toAnsi()
            );
        }

        if (this.hook) {
            MessageService.getInstance().info("uninstall.hook.start");
            GitService.getInstance().uninstallCommitWizardHook();
            MessageService.getInstance().success("uninstall.hook.success");
            return;
        }

        if (this.global) {
            MessageService.getInstance().info("uninstall.alias.global");
            GitService.getInstance().removeGitAliasGlobal();
        } else {
            MessageService.getInstance().info("uninstall.alias.local");
            GitService.getInstance().removeGitAliasLocal();
        }
        MessageService.getInstance().success("uninstall.success");
    }
}
