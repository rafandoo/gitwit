package dev.rafandoo.gitwit.cli;

import dev.rafandoo.gitwit.TestUtils;
import dev.rafandoo.gitwit.exception.GitWitException;
import dev.rafandoo.gitwit.mock.AbstractGitMock;
import dev.rafandoo.gitwit.mock.CommitMockFactory;
import dev.rafandoo.gitwit.service.ChangelogService;
import dev.rafandoo.gitwit.service.MessageService;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemErr;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Changelog Tests")
class ChangelogTest extends AbstractGitMock {

    @AfterEach
    void tearDown() {
        closeGitServiceMock();
    }

    @Test
    @Tag("integration")
    void shouldThrowExceptionOnWriteError() throws IOException {
        TestUtils.setupConfig(".changelog.gitwit");
        Changelog changelog = new Changelog();

        try (
            MockedStatic<MessageService> messageMock = mockStatic(MessageService.class);
            MockedStatic<ChangelogService> changelogMock = mockStatic(ChangelogService.class)
        ) {
            MessageService messageService = mock(MessageService.class);
            ChangelogService changelogService = mock(ChangelogService.class);

            messageMock.when(MessageService::getInstance).thenReturn(messageService);
            changelogMock.when(ChangelogService::getInstance).thenReturn(changelogService);

            when(changelogService.generateChangelog(any(), any(), any(), any()))
                .thenReturn(new StringBuilder("CHANGELOG CONTENT"));
            when(changelogService.writeChangeLog(anyString(), anyBoolean()))
                .thenThrow(new IOException("disk error"));

            assertThrows(GitWitException.class, changelog::run);
            verify(changelogService).writeChangeLog(anyString(), anyBoolean());
        }
    }

    @Test
    @Tag("integration")
    void shouldExecuteChangelogCLICommandSuccessfully(@TempDir Path tempDir) throws Exception {
        setupGitServiceMock();
        TestUtils.setupConfig(".changelog.gitwit");

        List<RevCommit> mockCommits = List.of(
            CommitMockFactory.mockCommit("1234", "feat: add new feature"),
            CommitMockFactory.mockCommit("5678", "fix: fix bug")
        );

        doReturn(mockCommits)
            .when(spyGitService)
            .getCommits(any(), any());

        doReturn(tempDir)
            .when(spyGitService)
            .getRepo();

        String[] args = {
            "changelog",
            "--from", "1234",
            "--to", "5678"
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
    void shouldCopyChangelogToClipboardSuccessfully() throws Exception {
        setupGitServiceMock();
        TestUtils.setupConfig(".changelog.gitwit");

        List<RevCommit> mockCommits = List.of(
            CommitMockFactory.mockCommit("1234", "feat: add new feature"),
            CommitMockFactory.mockCommit("5678", "fix: fix bug")
        );

        doReturn(mockCommits)
            .when(spyGitService)
            .getCommits(any(), any());

        String[] args = {
            "changelog",
            "--from", "1234",
            "--to", "5678",
            "--copy"
        };

        AtomicInteger exitCode = new AtomicInteger();
        String errText = tapSystemErr(() -> exitCode.set(TestUtils.executeCommand(args)));

        assertAll(
            () -> assertEquals(0, exitCode.get()),
            () -> assertTrue(errText.isBlank())
        );
    }
}
