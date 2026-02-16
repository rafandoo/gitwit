package dev.rafandoo.gitwit.cli;

import com.google.inject.Inject;
import dev.rafandoo.gitwit.exception.GitWitException;
import dev.rafandoo.gitwit.service.git.GitConfigService;
import dev.rafandoo.gitwit.service.git.GitHookService;
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

    @Inject
    private GitConfigService gitConfigService;

    @Inject
    private GitHookService gitHookService;

    @Override
    public void run() {
        if (this.hook && this.global) {
            throw new GitWitException("uninstall.error.conflict", true);
        }

        if (this.hook) {
            messageService.info("uninstall.hook.start");
            this.gitHookService.uninstallCommitWizardHook();
            messageService.success("uninstall.hook.success");
            return;
        }

        if (this.global) {
            messageService.info("uninstall.alias.global");
            this.gitConfigService.removeGitAliasGlobal();
        } else {
            messageService.info("uninstall.alias.local");
            this.gitConfigService.removeGitAliasLocal();
        }
        messageService.success("uninstall.success");
    }
}
