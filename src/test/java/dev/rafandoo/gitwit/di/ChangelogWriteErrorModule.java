package dev.rafandoo.gitwit.di;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import dev.rafandoo.gitwit.service.ChangelogService;
import dev.rafandoo.gitwit.service.GitService;
import dev.rafandoo.gitwit.service.I18nService;
import dev.rafandoo.gitwit.service.MessageService;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class ChangelogWriteErrorModule extends TestAppModule {

    @Provides
    @Singleton
    ChangelogService changelogService(
        MessageService messageService,
        GitService gitService,
        I18nService i18nService
    ) {
        ChangelogService real =
            new ChangelogService(messageService, gitService, i18nService);

        ChangelogService spy = spy(real);

        try {
            doReturn(new StringBuilder("CHANGELOG CONTENT"))
                .when(spy)
                .generateChangelog(any(), any(), any(), any());

            doThrow(IOException.class)
                .when(spy)
                .writeChangeLog(anyString(), anyBoolean());
        } catch (IOException ignored) {}

        return spy;
    }
}
