package dev.rafandoo.gitwit.cli;

import dev.rafandoo.gitwit.TestUtils;
import dev.rafandoo.gitwit.mock.AbstractGitMock;
import dev.rafandoo.gitwit.service.I18nService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemErr;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@DisplayName("Uninstall Command Tests")
public class UninstallTest extends AbstractGitMock {

    @AfterEach
    void tearDown() {
        closeGitServiceMock();
    }

    @Test
    @Tag("integration")
    void shouldUninstallGitAliasLocal(@TempDir Path tempDir) throws Exception {
        setupGitServiceMock();
        TestUtils.initTempGitRepo(tempDir);

        when(spyGitService.getRepo()).thenReturn(tempDir);

        String[] installArgs = {"install"};
        String errText = tapSystemErr(() -> {
            int exitCode = TestUtils.executeCommand(installArgs);
            assertEquals(0, exitCode);
        });
        assertTrue(errText.isBlank());

        String[] uninstallArgs = {"uninstall"};
        errText = tapSystemErr(() -> {
            int exitCode = TestUtils.executeCommand(uninstallArgs);
            assertEquals(0, exitCode);
        });

        assertTrue(errText.isBlank());

        verify(spyGitService).removeGitAliasLocal();
    }

    @Test
    @Tag("integration")
    void shouldUninstallGitAliasGlobal(@TempDir Path tempDir) throws Exception {
        setupGitServiceMock();
        TestUtils.initTempGitRepo(tempDir);

        when(spyGitService.getRepo()).thenReturn(tempDir);

        String[] installArgs = {
            "install",
            "--global"
        };
        String errText = tapSystemErr(() -> {
            int exitCode = TestUtils.executeCommand(installArgs);
            assertEquals(0, exitCode);
        });
        assertTrue(errText.isBlank());

        String[] uninstallArgs = {
            "uninstall",
            "--global"
        };
        errText = tapSystemErr(() -> {
            int exitCode = TestUtils.executeCommand(uninstallArgs);
            assertEquals(0, exitCode);
        });
        assertTrue(errText.isBlank());

        verify(spyGitService).removeGitAliasGlobal();
    }

    @Test
    @Tag("integration")
    void shouldUninstallCommitWizardHook(@TempDir Path tempDir) throws Exception {
        setupGitServiceMock();
        TestUtils.initTempGitRepo(tempDir);

        when(spyGitService.getRepo()).thenReturn(tempDir);

        String[] installArgs = {
            "install",
            "--hook"
        };
        String errText = tapSystemErr(() -> {
            int exitCode = TestUtils.executeCommand(installArgs);
            assertEquals(0, exitCode);
        });
        assertTrue(errText.isBlank());

        String[] uninstallArgs = {
            "uninstall",
            "--hook"
        };
        errText = tapSystemErr(() -> {
            int exitCode = TestUtils.executeCommand(uninstallArgs);
            assertEquals(0, exitCode);
        });
        assertTrue(errText.isBlank());

        verify(spyGitService, never()).configureGitAliasLocal();
        verify(spyGitService, never()).configureGitAliasGlobal();
        verify(spyGitService).setupCommitWizardHook(false);
        verify(spyGitService).uninstallCommitWizardHook();
    }

    @Test
    @Tag("integration")
    void shouldFailWhenHookAndGlobalOptionsAreUsedTogether(@TempDir Path tempDir) throws Exception {
        setupGitServiceMock();
        TestUtils.initTempGitRepo(tempDir);

        when(spyGitService.getRepo()).thenReturn(tempDir);

        String[] args = {
            "uninstall",
            "--hook",
            "--global"
        };

        String errText = tapSystemErr(() -> {
            int exitCode = TestUtils.executeCommand(args);
            assertEquals(1, exitCode);
        });

        assertTrue(errText.contains(I18nService.getInstance().getMessage("uninstall.conflict.hook_global")));
    }
}
