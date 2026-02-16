package dev.rafandoo.gitwit.service.changelog;

import dev.rafandoo.gitwit.config.GitWitConfig;
import dev.rafandoo.gitwit.enums.ConfigPaths;
import dev.rafandoo.gitwit.service.git.GitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChangelogWriter Tests")
class ChangelogWriterTest {

    @Mock
    GitService gitService;

    ChangelogWriter writer;

    @TempDir
    Path repoDir;

    @BeforeEach
    void setup() {
        when(this.gitService.getRepo())
            .thenReturn(this.repoDir);
        this.writer = new ChangelogWriter(this.gitService);
    }

    @Test
    void shouldWriteChangelogOverwritingExistingFile() throws IOException {
        Path result = this.writer.write("first content", false, new GitWitConfig());

        assertThat(Files.exists(result)).isTrue();
        assertThat(result.getFileName().toString())
            .isEqualTo(ConfigPaths.CHANGELOG_FILE.get().asString());

        String fileContent = Files.readString(result);
        assertThat(fileContent).isEqualTo("first content");
    }

    @Test
    void shouldAppendWithoutSeparatorWhenFileDoesNotExist() throws IOException {
        Path result = this.writer.write("initial content", true, new GitWitConfig());

        String fileContent = Files.readString(result);
        assertThat(fileContent).isEqualTo("initial content");
    }

    @Test
    void shouldAppendWithSeparatorWhenFileAlreadyExists() throws IOException {
        Path file = this.repoDir.resolve(ConfigPaths.CHANGELOG_FILE.get().asString());
        Files.writeString(file, "existing content");

        this.writer.write("new content", true, new GitWitConfig());

        String fileContent = Files.readString(file);
        assertThat(fileContent)
            .isEqualTo("existing content\n\nnew content");
    }
}
