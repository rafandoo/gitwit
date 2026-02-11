package dev.rafandoo.gitwit.cli;

import dev.rafandoo.gitwit.TestUtils;
import dev.rafandoo.gitwit.di.ChangelogWriteErrorModule;
import dev.rafandoo.gitwit.di.GuiceExtension;
import dev.rafandoo.gitwit.di.OverrideModules;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.atomic.AtomicInteger;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemErr;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(GuiceExtension.class)
@OverrideModules(ChangelogWriteErrorModule.class)
@DisplayName("Changelog Tests")
class ChangelogWriteErrorTest {

    @Test
    @Tag("integration")
    void shouldThrowExceptionOnWriteError() throws Exception {
        TestUtils.setupConfig(".changelog.gitwit");

        String[] args = {
            "changelog",
            "--from", "1234",
            "--to", "5678"
        };

        AtomicInteger exitCode = new AtomicInteger();
        String errText = tapSystemErr(() ->
            exitCode.set(TestUtils.executeCommand(args))
        );

        assertAll(
            () -> assertEquals(1, exitCode.get()),
            () -> assertFalse(errText.isBlank())
        );
    }
}
