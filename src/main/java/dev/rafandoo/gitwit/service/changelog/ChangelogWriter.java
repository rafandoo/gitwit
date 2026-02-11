package dev.rafandoo.gitwit.service.changelog;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rafandoo.gitwit.enums.ConfigPaths;
import dev.rafandoo.gitwit.service.git.GitService;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Service responsible for writing the changelog to a file.
 */
@Singleton
@AllArgsConstructor(onConstructor_ = @__({@Inject}))
public final class ChangelogWriter {

    private final GitService gitService;
    private static final String NL = "\n\n";

    /**
     * Writes the changelog content to the changelog file.
     *
     * @param content yhe changelog content to write.
     * @param append  whether to append to the existing file or overwrite it.
     * @return the path to the changelog file.
     * @throws IOException if an I/O error occurs.
     */
    public Path write(String content, boolean append) throws IOException {
        Path file = this.gitService.getRepo()
            .resolve(ConfigPaths.CHANGELOG_FILE.get().asString());

        if (append) {
            String sep = Files.exists(file) ? NL : "";
            Files.writeString(
                file,
                sep + content,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
            );
        } else {
            Files.writeString(
                file,
                content,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
            );
        }

        return file;
    }
}
