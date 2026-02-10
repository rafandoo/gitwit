package dev.rafandoo.gitwit.cli;

import com.google.inject.Inject;
import dev.rafandoo.gitwit.TestUtils;
import dev.rafandoo.gitwit.di.GuiceExtension;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(GuiceExtension.class)
@DisplayName("Uninstall Command Tests")
class UninstallTest {

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
    void shouldUninstallGitAliasLocal(@TempDir Path tempDir) throws Exception {
        TestUtils.initTempGitRepo(tempDir);

        doReturn(tempDir)
            .when(this.gitService)
            .getRepo();

        String[] installArgs = {"install"};

        AtomicInteger exitCodeInstall = new AtomicInteger();
        String errTextInstall = tapSystemErr(() -> exitCodeInstall.set(TestUtils.executeCommand(installArgs)));

        String[] uninstallArgs = {"uninstall"};

        AtomicInteger exitCode = new AtomicInteger();
        String errText = tapSystemErr(() -> exitCode.set(TestUtils.executeCommand(uninstallArgs)));

        assertThat(exitCodeInstall.get()).isEqualTo(0);
        assertThat(errTextInstall).isBlank();
        assertThat(exitCode.get()).isEqualTo(0);
        assertThat(errText).isBlank();

        verify(this.gitService).removeGitAliasLocal();
    }

    @Test
    @Tag("integration")
    void shouldUninstallGitAliasGlobal(@TempDir Path tempDir) throws Exception {
        TestUtils.initTempGitRepo(tempDir);

        doReturn(tempDir)
            .when(this.gitService)
            .getRepo();

        String[] installArgs = {
            "install",
            "--global"
        };

        AtomicInteger exitCodeInstall = new AtomicInteger();
        String errTextInstall = tapSystemErr(() -> exitCodeInstall.set(TestUtils.executeCommand(installArgs)));

        String[] uninstallArgs = {
            "uninstall",
            "--global"
        };

        AtomicInteger exitCode = new AtomicInteger();
        String errText = tapSystemErr(() -> exitCode.set(TestUtils.executeCommand(uninstallArgs)));

        assertThat(exitCodeInstall.get()).isEqualTo(0);
        assertThat(errTextInstall).isBlank();
        assertThat(exitCode.get()).isEqualTo(0);
        assertThat(errText).isBlank();

        verify(this.gitService).removeGitAliasGlobal();
    }

    @Test
    @Tag("integration")
    void shouldUninstallCommitWizardHook(@TempDir Path tempDir) throws Exception {
        TestUtils.initTempGitRepo(tempDir);

        doReturn(tempDir)
            .when(this.gitService)
            .getRepo();

        String[] installArgs = {
            "install",
            "--hook"
        };

        AtomicInteger exitCodeInstall = new AtomicInteger();
        String errTextInstall = tapSystemErr(() -> exitCodeInstall.set(TestUtils.executeCommand(installArgs)));

        String[] uninstallArgs = {
            "uninstall",
            "--hook"
        };

        AtomicInteger exitCode = new AtomicInteger();
        String errText = tapSystemErr(() -> exitCode.set(TestUtils.executeCommand(uninstallArgs)));

        assertThat(exitCodeInstall.get()).isEqualTo(0);
        assertThat(errTextInstall).isBlank();
        assertThat(exitCode.get()).isEqualTo(0);
        assertThat(errText).isBlank();

        verify(this.gitService, never()).configureGitAliasLocal();
        verify(this.gitService, never()).configureGitAliasGlobal();
        verify(this.gitService).setupCommitWizardHook(false);
        verify(this.gitService).uninstallCommitWizardHook();
    }

    @Test
    @Tag("integration")
    void shouldFailWhenHookAndGlobalOptionsAreUsedTogether(@TempDir Path tempDir) throws Exception {
        TestUtils.initTempGitRepo(tempDir);

        doReturn(tempDir)
            .when(this.gitService)
            .getRepo();

        String[] args = {
            "uninstall",
            "--hook",
            "--global"
        };

        AtomicInteger exitCode = new AtomicInteger();
        String errText = tapSystemErr(() -> exitCode.set(TestUtils.executeCommand(args)));

        assertThat(exitCode.get()).isEqualTo(1);
        assertThat(errText).contains(this.i18nService.getMessage("uninstall.error.conflict"));
    }
}
