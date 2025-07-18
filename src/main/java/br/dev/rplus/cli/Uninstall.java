package br.dev.rplus.cli;

import br.dev.rplus.enums.ExceptionMessage;
import br.dev.rplus.exception.GitWitException;
import br.dev.rplus.service.GitService;
import br.dev.rplus.service.MessageService;
import picocli.CommandLine;

/**
 * <h2>uninstall</h2>
 * <p>
 * Command that uninstall the GitWit automation for the current repository.
 */
@CommandLine.Command(
    name = "uninstall",
    description = "Uninstall the GitWit in the current repository."
)
public class Uninstall extends BaseCommand {

    @CommandLine.Option(
        names = {"-hk", "--hook"},
        description = "Uninstall GitWit as a prepare-commit-msg hook for the current repository."
    )
    private boolean hook;

    @CommandLine.Option(
        names = {"-g", "--global"},
        description = "Uninstall GitWit globally."
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
