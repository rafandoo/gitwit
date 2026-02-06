package dev.rafandoo.gitwit.cli;

import com.google.inject.Inject;
import dev.rafandoo.gitwit.TestUtils;
import dev.rafandoo.gitwit.cli.wiz.CommitWizard;
import dev.rafandoo.gitwit.di.GuiceExtension;
import dev.rafandoo.gitwit.entity.CommitMessage;
import dev.rafandoo.gitwit.service.I18nService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemErr;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(GuiceExtension.class)
@DisplayName("Hook Command Tests")
class HookTest {

    @Inject
    I18nService i18nService;

    @Test
    @Tag("integration")
    void shouldExecuteHookCommandSuccessfully(@TempDir Path tempDir) throws Exception {
        TestUtils.setupConfig(".general.gitwit");

        Path tempFile = tempDir.resolve("hook-file.txt");
        Files.createFile(tempFile);

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
            String[] args = {
                "hook",
                tempFile.toString()
            };

            AtomicInteger exitCode = new AtomicInteger();
            String errText = tapSystemErr(() -> exitCode.set(TestUtils.executeCommand(args)));

            assertAll(
                () -> assertEquals(0, exitCode.get()),
                () -> assertTrue(errText.isBlank())
            );
        }
    }

    @Test
    @Tag("integration")
    void shouldFailWhenCannotWriteFile() throws Exception {
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
            try (MockedStatic<Files> filesMock = mockStatic(Files.class, CALLS_REAL_METHODS)) {

                filesMock.when(() -> Files.writeString(any(), any()))
                    .thenThrow(new IOException("Simulated write error"));

                String[] args = {
                    "hook",
                    "file.txt"
                };

                AtomicInteger exitCode = new AtomicInteger();
                String errText = tapSystemErr(() -> exitCode.set(TestUtils.executeCommand(args)));

                assertAll(
                    () -> assertEquals(1, exitCode.get()),
                    () -> assertTrue(errText.contains(this.i18nService.getMessage("commit.hook.error.commit_write")))
                );
            }
        }
    }
}
