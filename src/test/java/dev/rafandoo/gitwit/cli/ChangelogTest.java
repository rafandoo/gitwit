package dev.rafandoo.gitwit.cli;

import com.google.inject.Inject;
import dev.rafandoo.gitwit.TestUtils;
import dev.rafandoo.gitwit.di.GuiceExtension;
import dev.rafandoo.gitwit.mock.CommitMockFactory;
import dev.rafandoo.gitwit.service.git.GitRepositoryService;
import dev.rafandoo.gitwit.service.git.GitService;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemErr;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(GuiceExtension.class)
@DisplayName("Changelog Tests")
class ChangelogTest {

    @Inject
    GitService gitService;

    @Inject
    GitRepositoryService gitRepositoryService;

    @BeforeEach
    void resetMocks() {
        reset(this.gitRepositoryService);
        clearInvocations(this.gitRepositoryService);
        reset(this.gitService);
    }

    @Test
    @Tag("integration")
    void shouldExecuteChangelogCLICommandSuccessfully(@TempDir Path tempDir) throws Exception {
        TestUtils.setupConfig(".changelog.gitwit");

        List<RevCommit> mockCommits = Arrays.asList(
            CommitMockFactory.mockCommit("1234", "feat: add new feature"),
            CommitMockFactory.mockCommit("5678", "fix: fix bug")
        );

        doReturn(mockCommits)
            .when(this.gitRepositoryService)
            .listCommitsBetween(any(), any());

        doReturn(tempDir)
            .when(this.gitService)
            .getRepo();

        String[] args = {
            "changelog",
            "1234..5678"
        };

        AtomicInteger exitCode = new AtomicInteger();
        String errText = tapSystemErr(() -> exitCode.set(TestUtils.executeCommand(args)));

        assertThat(exitCode.get()).isEqualTo(0);
        assertThat(errText).isBlank();
    }

    @Test
    @Tag("integration")
    void shouldCopyChangelogToClipboardSuccessfully() throws Exception {
        TestUtils.setupConfig(".changelog.gitwit");

        List<RevCommit> mockCommits = Arrays.asList(
            CommitMockFactory.mockCommit("1234", "feat: add new feature"),
            CommitMockFactory.mockCommit("5678", "fix: fix bug")
        );

        doReturn(mockCommits)
            .when(this.gitRepositoryService)
            .listCommitsBetween(any(), any());

        String[] args = {
            "changelog",
            "1234..5678",
            "--copy"
        };

        AtomicInteger exitCode = new AtomicInteger();
        String errText = tapSystemErr(() -> exitCode.set(TestUtils.executeCommand(args)));

        assertThat(exitCode.get()).isEqualTo(0);
        assertThat(errText).isBlank();
    }
}
