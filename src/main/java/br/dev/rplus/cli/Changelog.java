package br.dev.rplus.cli;

import br.dev.rplus.config.GitWitConfig;
import br.dev.rplus.enums.ExceptionMessage;
import br.dev.rplus.exception.GitWitException;
import br.dev.rplus.service.ChangelogService;
import br.dev.rplus.service.MessageService;
import br.dev.rplus.util.ClipboardUtil;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Path;

/**
 * <h2>changelog</h2>
 * <p>
 * Generates a changelog between two commits.
 * </p>
 * <p>
 * If no range is provided, the most recent commit (HEAD) is checked. If a range is provided
 * using {@code --from} and {@code --to}, all commits in the interval will be validated.
 * </p>
 */
@CommandLine.Command(
    name = "changelog",
    resourceBundle = "i18n.commands.changelog",
    sortOptions = false
)
public class Changelog extends BaseCommand {

    @CommandLine.Option(
        names = {"-f", "--from"},
        descriptionKey = "changelog.option.from",
        required = true
    )
    private String from;

    @CommandLine.Option(
        names = {"-t", "--to"},
        descriptionKey = "changelog.option.to"
    )
    private String to;

    @CommandLine.Option(
        names = {"-c", "--copy"},
        descriptionKey = "changelog.option.copy"
    )
    private boolean copyToClipboard;

    @Override
    public void run() {
        MessageService.getInstance().info("changelog.start");
        GitWitConfig config = loadConfig();

        StringBuilder changelogContent = ChangelogService.getInstance().generateChangelog(this.from, this.to, config);

        if (changelogContent != null) {
            MessageService.getInstance().info("changelog.generated");
            if (this.copyToClipboard) {
                if (ClipboardUtil.copyToClipboard(changelogContent.toString())) {
                    MessageService.getInstance().success("changelog.copied");
                }
            } else {
                try {
                    Path changelogPath = ChangelogService.getInstance().writeChangeLog(changelogContent.toString());
                    MessageService.getInstance().success("changelog.written", changelogPath);
                } catch (IOException e) {
                    throw new GitWitException(ExceptionMessage.CHANGELOG_FAILURE_WRITE, e);
                }
            }
        }
    }
}
