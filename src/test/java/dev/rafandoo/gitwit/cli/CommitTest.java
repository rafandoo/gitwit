package dev.rafandoo.gitwit.cli;

import com.google.inject.Inject;
import dev.rafandoo.gitwit.TestUtils;
import dev.rafandoo.gitwit.cli.wiz.CommitWizard;
import dev.rafandoo.gitwit.di.GuiceExtension;
import dev.rafandoo.gitwit.entity.CommitMessage;
import dev.rafandoo.gitwit.mock.CommitMockFactory;
import dev.rafandoo.gitwit.service.I18nService;
import dev.rafandoo.gitwit.service.git.GitCommitService;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;

import java.util.concurrent.atomic.AtomicInteger;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemErr;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(GuiceExtension.class)
@DisplayName("Commit Command Tests")
class CommitTest {

    @Inject
    GitCommitService gitCommitService;

    @Inject
    I18nService i18nService;

    @BeforeEach
    void resetMocks() {
        reset(this.gitCommitService);
        clearInvocations(this.gitCommitService);
    }

    @Test
    @Tag("integration")
    void shouldExecuteCommitSuccessfully() throws Exception {
        TestUtils.setupConfig(".general.gitwit");

        RevCommit commit = CommitMockFactory.mockCommit("abc123", "feat (core): Add new feature Z");
        doReturn(commit)
            .when(this.gitCommitService)
            .commit(any(CommitMessage.class), anyBoolean(), anyBoolean(), anyBoolean());

        String[] args = {
            "commit",
            "-t", "feat",
            "-s", "core",
            "-d", "Add new feature Z"
        };

        AtomicInteger exitCode = new AtomicInteger();
        String errText = tapSystemErr(() -> exitCode.set(TestUtils.executeCommand(args)));

        assertThat(exitCode.get()).isEqualTo(0);
        assertThat(errText).isBlank();
    }

    @Test
    @Tag("integration")
    void shouldFailWhenCommitIsNull() throws Exception {
        TestUtils.setupConfig(".general.gitwit");

        doReturn(null)
            .when(this.gitCommitService)
            .commit(any(CommitMessage.class), anyBoolean(), anyBoolean(), anyBoolean());

        String[] args = {
            "commit",
            "-t", "feat",
            "-s", "core",
            "-d", "Add new feature Z"
        };

        AtomicInteger exitCode = new AtomicInteger();
        String errText = tapSystemErr(() -> exitCode.set(TestUtils.executeCommand(args)));

        assertThat(exitCode.get()).isEqualTo(1);
        assertThat(errText).contains(this.i18nService.getMessage("commit.failure"));
    }

    @Test
    @Tag("integration")
    void shouldFailWhenCommitIsInvalid() throws Exception {
        TestUtils.setupConfig(".general.gitwit");

        String[] args = {
            "commit",
            "-t", "invalid-type",
            "-s", "core",
            "-d", "Add new feature Z"
        };

        AtomicInteger exitCode = new AtomicInteger();
        String errText = tapSystemErr(() -> exitCode.set(TestUtils.executeCommand(args)));

        assertThat(exitCode.get()).isEqualTo(1);
        assertThat(errText).contains(this.i18nService.getMessage("commit.validation.violations"));
    }

    @Test
    @Tag("integration")
    void shouldExecuteCommitWhenUseWizard() throws Exception {
        TestUtils.setupConfig(".general.gitwit");

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
            (wizardMock, context) -> when(wizardMock.run(any())).thenReturn(message)
        )) {
            RevCommit commit = CommitMockFactory.mockCommit("abc123", "feat (core): Add new feature Z");
            doReturn(commit)
                .when(this.gitCommitService)
                .commit(any(CommitMessage.class), anyBoolean(), anyBoolean(), anyBoolean());

            String[] args = {
                "commit",
            };

            AtomicInteger exitCode = new AtomicInteger();
            String errText = tapSystemErr(() -> exitCode.set(TestUtils.executeCommand(args)));

            assertThat(exitCode.get()).isEqualTo(0);
            assertThat(errText).isBlank();
        }
    }

}
