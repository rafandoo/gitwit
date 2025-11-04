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
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemErr;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Uninstall Command Tests")
class UninstallTest extends AbstractGitMock {

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

        AtomicInteger exitCodeInstall = new AtomicInteger();
        String errTextInstall = tapSystemErr(() -> exitCodeInstall.set(TestUtils.executeCommand(installArgs)));

        String[] uninstallArgs = {"uninstall"};

        AtomicInteger exitCode = new AtomicInteger();
        String errText = tapSystemErr(() -> exitCode.set(TestUtils.executeCommand(uninstallArgs)));

        assertAll(
            () -> assertEquals(0, exitCodeInstall.get()),
            () -> assertTrue(errTextInstall.isBlank()),
            () -> assertEquals(0, exitCode.get()),
            () -> assertTrue(errText.isBlank())
        );

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

        AtomicInteger exitCodeInstall = new AtomicInteger();
        String errTextInstall = tapSystemErr(() -> exitCodeInstall.set(TestUtils.executeCommand(installArgs)));

        String[] uninstallArgs = {
            "uninstall",
            "--global"
        };

        AtomicInteger exitCode = new AtomicInteger();
        String errText = tapSystemErr(() -> exitCode.set(TestUtils.executeCommand(uninstallArgs)));

        assertAll(
            () -> assertEquals(0, exitCodeInstall.get()),
            () -> assertTrue(errTextInstall.isBlank()),
            () -> assertEquals(0, exitCode.get()),
            () -> assertTrue(errText.isBlank())
        );

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

        AtomicInteger exitCodeInstall = new AtomicInteger();
        String errTextInstall = tapSystemErr(() -> exitCodeInstall.set(TestUtils.executeCommand(installArgs)));

        String[] uninstallArgs = {
            "uninstall",
            "--hook"
        };

        AtomicInteger exitCode = new AtomicInteger();
        String errText = tapSystemErr(() -> exitCode.set(TestUtils.executeCommand(uninstallArgs)));

        assertAll(
            () -> assertEquals(0, exitCodeInstall.get()),
            () -> assertTrue(errTextInstall.isBlank()),
            () -> assertEquals(0, exitCode.get()),
            () -> assertTrue(errText.isBlank())
        );

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

        AtomicInteger exitCode = new AtomicInteger();
        String errText = tapSystemErr(() -> exitCode.set(TestUtils.executeCommand(args)));

        assertAll(
            () -> assertEquals(1, exitCode.get()),
            () -> assertTrue(errText.contains(I18nService.getInstance().getMessage("uninstall.conflict.hook_global")))
        );
    }
}
