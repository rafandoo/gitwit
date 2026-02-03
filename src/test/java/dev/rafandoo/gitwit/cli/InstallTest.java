package dev.rafandoo.gitwit.cli;

import com.google.inject.Inject;
import dev.rafandoo.gitwit.TestUtils;
import dev.rafandoo.gitwit.di.GuiceExtension;
import dev.rafandoo.gitwit.exception.GitWitException;
import dev.rafandoo.gitwit.service.GitService;
import dev.rafandoo.gitwit.service.I18nService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemErr;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(GuiceExtension.class)
@DisplayName("Install Command Tests")
class InstallTest {

    @Inject
    GitService gitService;

    @Inject
    I18nService i18nService;

    @BeforeEach
    void resetMocks() {
        reset(this.gitService);
        clearInvocations(this.gitService);
    }

    @Test
    @Tag("integration")
    void shouldInstallGitAliasLocal(@TempDir Path tempDir) throws Exception {
        TestUtils.initTempGitRepo(tempDir);

        doReturn(tempDir)
            .when(this.gitService)
            .getRepo();

        String[] args = {"install"};

        AtomicInteger exitCode = new AtomicInteger();
        String errText = tapSystemErr(() -> exitCode.set(TestUtils.executeCommand(args)));

        assertAll(
            () -> assertEquals(0, exitCode.get()),
            () -> assertTrue(errText.isBlank())
        );

        verify(this.gitService).configureGitAliasLocal();
        verify(this.gitService, never()).configureGitAliasGlobal();
        verify(this.gitService, never()).setupCommitWizardHook(anyBoolean());
    }

    @Test
    @Tag("integration")
    void shouldInstallGitAliasGlobal(@TempDir Path tempDir) throws Exception {
        TestUtils.initTempGitRepo(tempDir);

        doReturn(tempDir)
            .when(this.gitService)
            .getRepo();

        String[] args = {
            "install",
            "--global"
        };

        AtomicInteger exitCode = new AtomicInteger();
        String errText = tapSystemErr(() -> exitCode.set(TestUtils.executeCommand(args)));

        assertAll(
            () -> assertEquals(0, exitCode.get()),
            () -> assertTrue(errText.isBlank())
        );

        verify(this.gitService).configureGitAliasGlobal();
        verify(this.gitService, never()).configureGitAliasLocal();
        verify(this.gitService, never()).setupCommitWizardHook(anyBoolean());
    }

    @Test
    @Tag("integration")
    void shouldInstallCommitWizardHook(@TempDir Path tempDir) throws Exception {
        TestUtils.initTempGitRepo(tempDir);

        doReturn(tempDir)
            .when(this.gitService)
            .getRepo();

        String[] args = {
            "install",
            "--hook"
        };

        AtomicInteger exitCode = new AtomicInteger();
        String errText = tapSystemErr(() -> exitCode.set(TestUtils.executeCommand(args)));

        assertAll(
            () -> assertEquals(0, exitCode.get()),
            () -> assertTrue(errText.isBlank())
        );

        verify(this.gitService).setupCommitWizardHook(false);
        verify(this.gitService, never()).configureGitAliasLocal();
        verify(this.gitService, never()).configureGitAliasGlobal();
    }

    @Test
    @Tag("integration")
    void shouldFailWhenHookAndGlobalOptionsAreUsedTogether(@TempDir Path tempDir) throws Exception {
        TestUtils.initTempGitRepo(tempDir);

        doReturn(tempDir)
            .when(this.gitService)
            .getRepo();

        String[] args = {
            "install",
            "--hook",
            "--global"
        };

        AtomicInteger exitCode = new AtomicInteger();
        String errText = tapSystemErr(() -> exitCode.set(TestUtils.executeCommand(args)));

        assertAll(
            () -> assertEquals(1, exitCode.get()),
            () -> assertTrue(errText.contains(this.i18nService.getMessage("install.error.conflict")))
        );

        verify(this.gitService, never()).configureGitAliasGlobal();
        verify(this.gitService, never()).configureGitAliasLocal();
        verify(this.gitService, never()).setupCommitWizardHook(anyBoolean());
    }

    @Test
    @Tag("integration")
    void shouldForceInstallCommitWizardHook(@TempDir Path tempDir) throws Exception {
        TestUtils.initTempGitRepo(tempDir);

        doReturn(tempDir)
            .when(this.gitService)
            .getRepo();

        String[] args = {
            "install",
            "--hook",
            "--force"
        };

        AtomicInteger exitCode = new AtomicInteger();
        String errText = tapSystemErr(() -> exitCode.set(TestUtils.executeCommand(args)));

        assertAll(
            () -> assertEquals(0, exitCode.get()),
            () -> assertTrue(errText.isBlank())
        );

        verify(this.gitService).setupCommitWizardHook(true);
        verify(this.gitService, never()).configureGitAliasLocal();
        verify(this.gitService, never()).configureGitAliasGlobal();
    }

    @Test
    @Tag("integration")
    void shouldFailWhenNoGitRepositoryFound(@TempDir Path tempDir) throws Exception {
        doReturn(tempDir)
            .when(this.gitService)
            .getRepo();

        doThrow(new GitWitException(
            "git.error.not_a_repo"
        ))
            .when(this.gitService)
            .setupCommitWizardHook(anyBoolean());

        String[] args = {
            "install",
            "--hook"
        };

        AtomicInteger exitCode = new AtomicInteger();
        String errText = tapSystemErr(() -> exitCode.set(TestUtils.executeCommand(args)));

        assertAll(
            () -> assertEquals(1, exitCode.get()),
            () -> assertTrue(errText.contains(this.i18nService.getMessage("git.error.not_a_repo")))
        );

        verify(this.gitService).setupCommitWizardHook(false);
        verify(this.gitService, never()).configureGitAliasLocal();
        verify(this.gitService, never()).configureGitAliasGlobal();
    }
}
