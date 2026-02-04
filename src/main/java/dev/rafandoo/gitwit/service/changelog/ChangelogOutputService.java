package dev.rafandoo.gitwit.service.changelog;

import dev.rafandoo.gitwit.exception.GitWitException;
import dev.rafandoo.gitwit.service.MessageService;
import dev.rafandoo.gitwit.util.ClipboardUtil;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Service responsible for outputting changelog content either to the clipboard or to a file.
 */
@Singleton
@AllArgsConstructor(onConstructor_ = @__({@Inject}))
public class ChangelogOutputService {

    private final ChangelogWriter writer;
    private MessageService messageService;

    /**
     * Outputs the changelog content either by copying it to the clipboard or writing it to a file.
     *
     * @param content the changelog content to output.
     * @param copy    if {@code true}, copies the content to the clipboard; otherwise, writes it to a file.
     * @param append  if {@code true} and writing to a file, appends the content; otherwise, overwrites the file.
     */
    public void output(String content, boolean copy, boolean append) {
        try {
            if (copy) {
                if (!ClipboardUtil.copyToClipboard(content)) {
                    throw new GitWitException("changelog.error.clipboard");
                }
                this.messageService.info("changelog.copied");
            } else {
                Path path = this.writer.write(content, append);
                messageService.success("changelog.written", path);
            }
        } catch (IOException e) {
            throw new GitWitException("changelog.error.write", e);
        }
    }
}
