package dev.rafandoo.gitwit;

import dev.rafandoo.gitwit.mock.AbstractGitMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@DisplayName("App Command Tests")
class AppTest extends AbstractGitMock {

    @Test
    @Tag("integration")
    void shouldShowHelpWhenNoArgumentsProvided() throws Exception {
        String[] args = {};

        String outText = tapSystemOut(() -> {
            int exitCode = TestUtils.executeCommand(args);
            assertEquals(0, exitCode);
        });

        assertTrue(outText.contains("GitWit"));
    }

    @Test
    @Tag("integration")
    void shouldShowVersionWhenVersionOptionProvided() throws Exception {
        String[] args = {
            "--version"
        };

        String outText = tapSystemOut(() -> {
            int exitCode = TestUtils.executeCommand(args);
            assertEquals(0, exitCode);
        });

        assertTrue(outText.contains("dev‑snapshot"));
    }

    @Test
    @Tag("integration")
    void shouldGenerateConfigExample(@TempDir Path tempDir) {
        try {
            setupGitServiceMock();

            doReturn(tempDir)
                .when(spyGitService)
                .getRepo();

            String[] args = {
                "--config-example"
            };

            int exitCode = TestUtils.executeCommand(args);
            assertEquals(0, exitCode);
        } finally {
            closeGitServiceMock();
        }
    }

    @Test
    void shouldDoesntThrowExceptionWhenNoArgsProvided() {
        String[] args = {};
        assertDoesNotThrow(() -> App.main(args));
    }

    @Test
    void shouldDoesntThrowExceptionWhenVersionArgProvided() {
        String[] args = {
            "--debug"
        };
        assertDoesNotThrow(() -> App.main(args));
    }
}
