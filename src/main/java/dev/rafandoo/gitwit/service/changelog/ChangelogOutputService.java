package dev.rafandoo.gitwit.service.changelog;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rafandoo.gitwit.config.GitWitConfig;
import dev.rafandoo.gitwit.exception.GitWitException;
import dev.rafandoo.gitwit.service.MessageService;
import dev.rafandoo.gitwit.service.TerminalService;
import dev.rafandoo.gitwit.util.ClipboardUtil;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;

/**
 * Service responsible for outputting changelog content either to the clipboard or to a file.
 */
@Singleton
@AllArgsConstructor(onConstructor_ = @__({@Inject}))
public final class ChangelogOutputService {

    private final ChangelogWriter writer;
    private final MessageService messageService;
    private final TerminalService terminalService;

    /**
     * Outputs the changelog content either by copying it to the clipboard or writing it to a file.
     *
     * @param content the changelog content to output.
     * @param copy    if {@code true}, copies the content to the clipboard; otherwise, writes it to a file.
     * @param append  if {@code true} and writing to a file, appends the content; otherwise, overwrites the file.
     * @param config  the GitWit configuration containing changelog settings.
     * @param stdout   if {@code true}, indicates that the output is intended for standard output.
     */
    public void output(String content, boolean copy, boolean append, GitWitConfig config, boolean stdout) {
        try {
            if (stdout) {
                try (PrintWriter writer = this.terminalService.getTerminal().writer()) {
                    writer.println(content);
                }
                return;
            }
            if (copy) {
                if (!ClipboardUtil.copyToClipboard(content)) {
                    throw new GitWitException("changelog.error.clipboard");
                }
                this.messageService.info("changelog.copied");
            } else {
                Path path = this.writer.write(content, append, config);
                messageService.success("changelog.written", path);
            }
        } catch (IOException e) {
            throw new GitWitException("changelog.error.write", e);
        }
    }
}
