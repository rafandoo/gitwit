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

@DisplayName("Install Command Tests")
class InstallTest extends AbstractGitMock {

    @AfterEach
    void tearDown() {
        closeGitServiceMock();
    }

    @Test
    @Tag("integration")
    void shouldInstallGitAliasLocal(@TempDir Path tempDir) throws Exception {
        setupGitServiceMock();
        TestUtils.initTempGitRepo(tempDir);

        when(spyGitService.getRepo()).thenReturn(tempDir);

        String[] args = {"install"};

        String errText = tapSystemErr(() -> {
            int exitCode = TestUtils.executeCommand(args);
            assertEquals(0, exitCode);
        });

        assertTrue(errText.isBlank());

        verify(spyGitService).configureGitAliasLocal();
        verify(spyGitService, never()).configureGitAliasGlobal();
        verify(spyGitService, never()).setupCommitWizardHook(anyBoolean());
    }

    @Test
    @Tag("integration")
    void shouldInstallGitAliasGlobal(@TempDir Path tempDir) throws Exception {
        setupGitServiceMock();
        TestUtils.initTempGitRepo(tempDir);

        when(spyGitService.getRepo()).thenReturn(tempDir);

        String[] args = {
            "install",
            "--global"
        };

        String errText = tapSystemErr(() -> {
            int exitCode = TestUtils.executeCommand(args);
            assertEquals(0, exitCode);
        });

        assertTrue(errText.isBlank());

        verify(spyGitService).configureGitAliasGlobal();
        verify(spyGitService, never()).configureGitAliasLocal();
        verify(spyGitService, never()).setupCommitWizardHook(anyBoolean());
    }

    @Test
    @Tag("integration")
    void shouldInstallCommitWizardHook(@TempDir Path tempDir) throws Exception {
        setupGitServiceMock();
        TestUtils.initTempGitRepo(tempDir);

        when(spyGitService.getRepo()).thenReturn(tempDir);

        String[] args = {
            "install",
            "--hook"
        };

        String errText = tapSystemErr(() -> {
            int exitCode = TestUtils.executeCommand(args);
            assertEquals(0, exitCode);
        });

        assertTrue(errText.isBlank());

        verify(spyGitService).setupCommitWizardHook(false);
        verify(spyGitService, never()).configureGitAliasLocal();
        verify(spyGitService, never()).configureGitAliasGlobal();
    }

    @Test
    @Tag("integration")
    void shouldFailWhenHookAndGlobalOptionsAreUsedTogether(@TempDir Path tempDir) throws Exception {
        setupGitServiceMock();
        TestUtils.initTempGitRepo(tempDir);

        when(spyGitService.getRepo()).thenReturn(tempDir);

        String[] args = {
            "install",
            "--hook",
            "--global"
        };

        String errText = tapSystemErr(() -> {
            int exitCode = TestUtils.executeCommand(args);
            assertEquals(1, exitCode);
        });

        assertTrue(errText.contains(I18nService.getInstance().getMessage("install.conflict.hook_global")));

        verify(spyGitService, never()).configureGitAliasGlobal();
        verify(spyGitService, never()).configureGitAliasLocal();
        verify(spyGitService, never()).setupCommitWizardHook(anyBoolean());
    }

    @Test
    @Tag("integration")
    void shouldForceInstallCommitWizardHook(@TempDir Path tempDir) throws Exception {
        setupGitServiceMock();
        TestUtils.initTempGitRepo(tempDir);

        when(spyGitService.getRepo()).thenReturn(tempDir);

        String[] args = {
            "install",
            "--hook",
            "--force"
        };

        String errText = tapSystemErr(() -> {
            int exitCode = TestUtils.executeCommand(args);
            assertEquals(0, exitCode);
        });

        assertTrue(errText.isBlank());

        verify(spyGitService).setupCommitWizardHook(true);
        verify(spyGitService, never()).configureGitAliasLocal();
        verify(spyGitService, never()).configureGitAliasGlobal();
    }

    @Test
    @Tag("integration")
    void shouldFailWhenNoGitRepositoryFound(@TempDir Path tempDir) throws Exception {
        setupGitServiceMock();
        when(spyGitService.getRepo()).thenReturn(tempDir);

        String[] args = {
            "install",
            "--hook"
        };

        String errText = tapSystemErr(() -> {
            int exitCode = TestUtils.executeCommand(args);
            assertEquals(1, exitCode);
        });

        assertTrue(errText.contains(I18nService.getInstance().getMessage("error.not_a_git_repository")));

        verify(spyGitService).setupCommitWizardHook(false);
        verify(spyGitService, never()).configureGitAliasLocal();
        verify(spyGitService, never()).configureGitAliasGlobal();
    }
}
