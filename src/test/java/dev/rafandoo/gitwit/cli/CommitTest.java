package dev.rafandoo.gitwit.cli;

import dev.rafandoo.gitwit.TestUtils;
import dev.rafandoo.gitwit.cli.wiz.CommitWizard;
import dev.rafandoo.gitwit.entity.CommitMessage;
import dev.rafandoo.gitwit.enums.ExceptionMessage;
import dev.rafandoo.gitwit.mock.AbstractGitMock;
import dev.rafandoo.gitwit.mock.CommitMockFactory;
import dev.rafandoo.gitwit.service.I18nService;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.util.concurrent.atomic.AtomicInteger;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemErr;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Commit Command Tests")
class CommitTest extends AbstractGitMock {

    @AfterEach
    void tearDown() {
        closeGitServiceMock();
    }

    @Test
    @Tag("integration")
    void shouldExecuteCommitSuccessfully() throws Exception {
        TestUtils.setupConfig(".general.gitwit");
        setupGitServiceMock();

        RevCommit commit = CommitMockFactory.mockCommit("abc123", "feat (core): Add new feature Z");
        doReturn(commit).when(spyGitService).commit(any(CommitMessage.class), anyBoolean(), anyBoolean());

        String[] args = {
            "commit",
            "-t", "feat",
            "-s", "core",
            "-d", "Add new feature Z"
        };

        AtomicInteger exitCode = new AtomicInteger();
        String errText = tapSystemErr(() -> exitCode.set(TestUtils.executeCommand(args)));

        assertAll(
            () -> assertEquals(0, exitCode.get()),
            () -> assertTrue(errText.isBlank())
        );
    }

    @Test
    @Tag("integration")
    void shouldFailWhenCommitIsNull() throws Exception {
        TestUtils.setupConfig(".general.gitwit");
        setupGitServiceMock();

        doReturn(null).when(spyGitService).commit(any(CommitMessage.class), anyBoolean(), anyBoolean());

        String[] args = {
            "commit",
            "-t", "feat",
            "-s", "core",
            "-d", "Add new feature Z"
        };

        AtomicInteger exitCode = new AtomicInteger();
        String errText = tapSystemErr(() -> exitCode.set(TestUtils.executeCommand(args)));

        assertAll(
            () -> assertEquals(1, exitCode.get()),
            () -> assertTrue(errText.contains(I18nService.getInstance().getMessage(ExceptionMessage.COMMIT_EXECUTION_FAILED.getMessage())))
        );
    }

    @Test
    @Tag("integration")
    void shouldFailWhenCommitIsInvalid() throws Exception {
        TestUtils.setupConfig(".general.gitwit");
        setupGitServiceMock();

        String[] args = {
            "commit",
            "-t", "invalid-type",
            "-s", "core",
            "-d", "Add new feature Z"
        };

        AtomicInteger exitCode = new AtomicInteger();
        String errText = tapSystemErr(() -> exitCode.set(TestUtils.executeCommand(args)));

        assertAll(
            () -> assertEquals(1, exitCode.get()),
            () -> assertTrue(errText.contains(I18nService.getInstance().getMessage("commit.validation.violations")))
        );
    }

    @Test
    @Tag("integration")
    void shouldExecuteCommitWhenUseWizard() throws Exception {
        TestUtils.setupConfig(".general.gitwit");
        setupGitServiceMock();

        CommitMessage message = new CommitMessage(
            "feat",
            "core",
            "Add new feature Z",
            null,
            false,
            null,
            null,
            null
        );

        try (MockedConstruction<CommitWizard> mockWizard = mockConstruction(
            CommitWizard.class,
            (wizardMock, context) -> when(wizardMock.run()).thenReturn(message)
        )) {
            RevCommit commit = CommitMockFactory.mockCommit("abc123", "feat (core): Add new feature Z");
            doReturn(commit).when(spyGitService).commit(any(CommitMessage.class), anyBoolean(), anyBoolean());

            String[] args = {
                "commit",
            };

            AtomicInteger exitCode = new AtomicInteger();
            String errText = tapSystemErr(() -> exitCode.set(TestUtils.executeCommand(args)));

            assertAll(
                () -> assertEquals(0, exitCode.get()),
                () -> assertTrue(errText.isBlank())
            );
        }
    }

}
