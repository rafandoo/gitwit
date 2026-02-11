package dev.rafandoo.gitwit.cli;

import com.google.inject.Inject;
import dev.rafandoo.gitwit.TestUtils;
import dev.rafandoo.gitwit.di.GuiceExtension;
import dev.rafandoo.gitwit.exception.GitWitException;
import dev.rafandoo.gitwit.service.git.GitService;
import dev.rafandoo.gitwit.service.I18nService;
import dev.rafandoo.gitwit.service.git.GitConfigService;
import dev.rafandoo.gitwit.service.git.GitHookService;
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
@DisplayName("Install Command Tests")
class InstallTest {

    @Inject
    GitService gitService;

    @Inject
    GitConfigService gitConfigService;

    @Inject
    GitHookService gitHookService;

    @Inject
    I18nService i18nService;

    @BeforeEach
    void resetMocks() {
        reset(this.gitService);
        clearInvocations(this.gitService);
        clearInvocations(this.gitConfigService);
        clearInvocations(this.gitHookService);
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

        assertThat(exitCode.get()).isEqualTo(0);
        assertThat(errText).isBlank();

        verify(this.gitConfigService).configureGitAliasLocal();
        verify(this.gitConfigService, never()).configureGitAliasGlobal();
        verify(this.gitHookService, never()).setupCommitWizardHook(anyBoolean());
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

        assertThat(exitCode.get()).isEqualTo(0);
        assertThat(errText).isBlank();

        verify(this.gitConfigService).configureGitAliasGlobal();
        verify(this.gitConfigService, never()).configureGitAliasLocal();
        verify(this.gitHookService, never()).setupCommitWizardHook(anyBoolean());
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

        assertThat(exitCode.get()).isEqualTo(0);
        assertThat(errText).isBlank();

        verify(this.gitHookService).setupCommitWizardHook(false);
        verify(this.gitConfigService, never()).configureGitAliasLocal();
        verify(this.gitConfigService, never()).configureGitAliasGlobal();
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

        assertThat(exitCode.get()).isEqualTo(1);
        assertThat(errText).contains(this.i18nService.getMessage("install.error.conflict"));

        verify(this.gitConfigService, never()).configureGitAliasGlobal();
        verify(this.gitConfigService, never()).configureGitAliasLocal();
        verify(this.gitHookService, never()).setupCommitWizardHook(anyBoolean());
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

        assertThat(exitCode.get()).isEqualTo(0);
        assertThat(errText).isBlank();

        verify(this.gitHookService).setupCommitWizardHook(true);
        verify(this.gitConfigService, never()).configureGitAliasLocal();
        verify(this.gitConfigService, never()).configureGitAliasGlobal();
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
            .when(this.gitHookService)
            .setupCommitWizardHook(anyBoolean());

        String[] args = {
            "install",
            "--hook"
        };

        AtomicInteger exitCode = new AtomicInteger();
        String errText = tapSystemErr(() -> exitCode.set(TestUtils.executeCommand(args)));

        assertThat(exitCode.get()).isEqualTo(1);
        assertThat(errText).contains(this.i18nService.getMessage("git.error.not_a_repo"));

        verify(this.gitHookService).setupCommitWizardHook(false);
        verify(this.gitConfigService, never()).configureGitAliasLocal();
        verify(this.gitConfigService, never()).configureGitAliasGlobal();
    }
}
