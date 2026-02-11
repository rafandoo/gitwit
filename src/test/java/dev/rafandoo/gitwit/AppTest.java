package dev.rafandoo.gitwit;

import com.google.inject.Inject;
import dev.rafandoo.gitwit.di.GuiceExtension;
import dev.rafandoo.gitwit.service.git.GitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.*;

@ExtendWith(GuiceExtension.class)
@DisplayName("App Command Tests")
class AppTest {

    @Inject
    GitService gitService;

    @BeforeEach
    void resetMocks() {
        reset(this.gitService);
        clearInvocations(this.gitService);
    }

    @Test
    @Tag("integration")
    void shouldShowHelpWhenNoArgumentsProvided() throws Exception {
        String[] args = {};

        String outText = tapSystemOut(() -> {
            int exitCode = TestUtils.executeCommand(args);
            assertThat(exitCode).isEqualTo(0);
        });

        assertThat(outText).contains("GitWit");
    }

    @Test
    @Tag("integration")
    void shouldShowVersionWhenVersionOptionProvided() throws Exception {
        String[] args = {
            "--version"
        };

        String outText = tapSystemOut(() -> {
            int exitCode = TestUtils.executeCommand(args);
            assertThat(exitCode).isEqualTo(0);
        });

        assertThat(outText).contains("dev‑snapshot");
    }

    @Test
    @Tag("integration")
    void shouldGenerateConfigExample(@TempDir Path tempDir) {
        doReturn(tempDir)
            .when(this.gitService)
            .getRepo();

        String[] args = {
            "--config-example"
        };

        int exitCode = TestUtils.executeCommand(args);
        assertThat(exitCode).isEqualTo(0);
    }

    @Test
    void shouldDoesntThrowExceptionWhenNoArgsProvided() {
        String[] args = {};
        assertThatNoException()
            .isThrownBy(
                () -> App.main(args)
            );
    }
}
