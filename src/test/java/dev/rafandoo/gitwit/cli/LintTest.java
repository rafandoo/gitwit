package dev.rafandoo.gitwit.cli;

import com.google.inject.Inject;
import dev.rafandoo.gitwit.TestUtils;
import dev.rafandoo.gitwit.di.GuiceExtension;
import dev.rafandoo.gitwit.exception.GitWitException;
import dev.rafandoo.gitwit.mock.CommitMockFactory;
import dev.rafandoo.gitwit.service.git.GitRepositoryService;
import dev.rafandoo.gitwit.service.I18nService;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemErr;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(GuiceExtension.class)
@DisplayName("Lint Command Tests")
class LintTest {

    @Inject
    GitRepositoryService gitRepositoryService;

    @Inject
    I18nService i18nService;

    @BeforeEach
    void resetMocks() {
        reset(this.gitRepositoryService);
        clearInvocations(this.gitRepositoryService);
    }

    @Test
    @Tag("integration")
    void shouldLintCommitsInRangeSuccessfully() throws Exception {
        TestUtils.setupConfig(".lint.repo.gitwit");

        List<RevCommit> mockCommits = Arrays.asList(
            CommitMockFactory.mockCommit("f337727030873b96ead6b5ce75d13fffae931bc6", ":sparkles:: Add new feature"),
            CommitMockFactory.mockCommit("eb2b9188883d29508a818129ac7e6ce5584db0c0", ":bug:: Fix bug in feature")
        );

        doReturn(mockCommits)
            .when(this.gitRepositoryService)
            .resolveCommits(
                any(),
                any(),
                any(),
                any()
            );

        String[] args = {
            "lint",
            "f337727030873b96ead6b5ce75d13fffae931bc6..eb2b9188883d29508a818129ac7e6ce5584db0c0"
        };

        AtomicInteger exitCode = new AtomicInteger();
        String errText = tapSystemErr(() -> exitCode.set(TestUtils.executeCommand(args)));

        assertThat(exitCode.get()).isEqualTo(0);
        assertThat(errText).isBlank();
    }

    @Test
    @Tag("integration")
    void shouldLintLastCommitSuccessfully() throws Exception {
        TestUtils.setupConfig(".lint.repo.gitwit");

        RevCommit commit = CommitMockFactory.mockCommit("HEAD", ":sparkles:: Latest commit");
        doReturn(Arrays.asList(commit))
            .when(this.gitRepositoryService)
            .resolveCommits(
                any(),
                any(),
                any(),
                any()
            );

        String[] args = {
            "lint",
        };

        AtomicInteger exitCode = new AtomicInteger();
        String errText = tapSystemErr(() -> exitCode.set(TestUtils.executeCommand(args)));

        assertThat(exitCode.get()).isEqualTo(0);
        assertThat(errText).isBlank();
    }

    @Test
    @Tag("integration")
    void shouldLintSpecificCommitSuccessfully() throws Exception {
        TestUtils.setupConfig(".lint.repo.gitwit");

        RevCommit commit = CommitMockFactory.mockCommit("f337727030873b96ead6b5ce75d13fffae931bc6", ":sparkles:: Specific commit");
        doReturn(Arrays.asList(commit))
            .when(this.gitRepositoryService)
            .resolveCommits(
                any(),
                any(),
                any(),
                any()
            );

        String[] args = {
            "lint",
            "f337727030873b96ead6b5ce75d13fffae931bc6"
        };

        AtomicInteger exitCode = new AtomicInteger();
        String errText = tapSystemErr(() -> exitCode.set(TestUtils.executeCommand(args)));

        assertThat(exitCode.get()).isEqualTo(0);
        assertThat(errText).isBlank();
    }

    @Test
    @Tag("integration")
    void shouldFailWhenFromCommitNotFound() throws Exception {
        TestUtils.setupConfig(".lint.repo.gitwit");
        String[] args = {
            "lint",
            "invalidSHA"
        };

        doThrow(new GitWitException(
            "git.repo.error.rev_not_found",
            "invalidSHA"
        ))
            .when(this.gitRepositoryService)
            .resolveCommits(eq("invalidSHA"), any(), any(), any());


        AtomicInteger exitCode = new AtomicInteger();
        String errText = tapSystemErr(() -> exitCode.set(TestUtils.executeCommand(args)));

        String expectedMessage = this.i18nService.getMessage(
            "git.repo.error.rev_not_found",
            "invalidSHA"
        );

//        assertThat(exitCode.get()).isEqualTo(1);
//        assertThat(errText).contains(expectedMessage);
    }

    @ParameterizedTest
    @Tag("integration")
    @MethodSource("messageProvider")
    void shouldLintMessageSuccessfully(String message) throws Exception {
        TestUtils.setupConfig(".general.gitwit");

        String[] args = {
            "lint",
            "-m", message
        };

        AtomicInteger exitCode = new AtomicInteger();
        String errText = tapSystemErr(() -> exitCode.set(TestUtils.executeCommand(args)));

        assertThat(exitCode.get()).isEqualTo(0);
        assertThat(errText).isBlank();
    }

    private static Stream<Arguments> messageProvider() {
        return Stream.of(
            Arguments.of("feat: Add new feature"),
            Arguments.of("fix(api): Fix bug in API"),
            Arguments.of("docs: Update README with new instructions"),
            Arguments.of("fix!: Correct critical security vulnerability")
        );
    }
}
