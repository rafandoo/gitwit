package dev.rafandoo.gitwit.cli;

import dev.rafandoo.gitwit.TestUtils;
import dev.rafandoo.gitwit.mock.AbstractGitMock;
import dev.rafandoo.gitwit.mock.CommitMockFactory;
import dev.rafandoo.gitwit.service.I18nService;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemErr;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Lint Command Tests")
class LintTest extends AbstractGitMock {

    @AfterEach
    void tearDown() {
        closeGitServiceMock();
    }

    @Test
    @Tag("integration")
    void shouldLintCommitsInRangeSuccessfully() throws Exception {
        setupGitServiceMock();
        TestUtils.setupConfig(".lint.repo.gitwit");

        List<RevCommit> mockCommits = List.of(
            CommitMockFactory.mockCommit("f337727030873b96ead6b5ce75d13fffae931bc6", ":sparkles:: Add new feature"),
            CommitMockFactory.mockCommit("eb2b9188883d29508a818129ac7e6ce5584db0c0", ":bug:: Fix bug in feature")
        );

        when(spyGitService.getCommits(any(), any())).thenReturn(mockCommits);

        String[] args = {
            "lint",
            "--from", "f337727030873b96ead6b5ce75d13fffae931bc6",
            "--to", "eb2b9188883d29508a818129ac7e6ce5584db0c0"
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
    void shouldLintLastCommitSuccessfully() throws Exception {
        setupGitServiceMock();
        TestUtils.setupConfig(".lint.repo.gitwit");

        RevCommit commit = CommitMockFactory.mockCommit("HEAD", ":sparkles:: Latest commit");
        when(spyGitService.getCommits(null, null)).thenReturn(List.of(commit));

        String[] args = {"lint"};

        AtomicInteger exitCode = new AtomicInteger();
        String errText = tapSystemErr(() -> exitCode.set(TestUtils.executeCommand(args)));

        assertAll(
            () -> assertEquals(0, exitCode.get()),
            () -> assertTrue(errText.isBlank())
        );
    }

    @Test
    @Tag("integration")
    void shouldFailWhenFromCommitNotFound() throws Exception {
        TestUtils.setupConfig(".lint.repo.gitwit");
        String[] args = {"lint", "--from", "invalidSHA"};

        AtomicInteger exitCode = new AtomicInteger();
        String errText = tapSystemErr(() -> exitCode.set(TestUtils.executeCommand(args)));

        String expectedMessage = I18nService.getInstance().getMessage(
            "error.git.rev_spec_not_found",
            "invalidSHA"
        );

        assertAll(
            () -> assertEquals(1, exitCode.get()),
            () -> assertTrue(errText.contains(expectedMessage))
        );
    }
}
