package dev.rafandoo.gitwit.service.changelog;

import dev.rafandoo.gitwit.exception.GitWitException;
import dev.rafandoo.gitwit.service.I18nService;
import dev.rafandoo.gitwit.service.MessageService;
import dev.rafandoo.gitwit.util.ClipboardUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChangelogOutputService Tests")
class ChangelogOutputServiceTest {

    @Mock
    ChangelogWriter writer;

    @Mock
    MessageService messageService;

    ChangelogOutputService service;

    I18nService i18nService = new I18nService();

    @BeforeEach
    void setup() {
        this.service = new ChangelogOutputService(this.writer, this.messageService);
    }

    @Test
    void shouldCopyToClipboardSuccessfully() {
        try (MockedStatic<ClipboardUtil> clipboardMock = mockStatic(ClipboardUtil.class)) {
            clipboardMock.when(() -> ClipboardUtil.copyToClipboard("content"))
                .thenReturn(true);

            this.service.output("content", true, false);

            verify(this.messageService).info("changelog.copied");
            verifyNoInteractions(this.writer);
        }
    }

    @Test
    void shouldThrowExceptionWhenClipboardCopyFails() {
        try (MockedStatic<ClipboardUtil> clipboardMock = mockStatic(ClipboardUtil.class)) {
            clipboardMock.when(() -> ClipboardUtil.copyToClipboard("content"))
                .thenReturn(false);

            assertThatThrownBy(() ->
                this.service.output("content", true, false)
            )
                .isInstanceOf(GitWitException.class)
                .hasMessage(this.i18nService.getMessage("changelog.error.clipboard"));

            verifyNoInteractions(this.writer);
            verifyNoInteractions(this.messageService);
        }
    }

    @Test
    void shouldWriteChangelogToFileSuccessfully() throws IOException {
        Path path = Path.of("CHANGELOG.md");
        when(this.writer.write("content", true)).thenReturn(path);

        this.service.output("content", false, true);

        verify(this.writer).write("content", true);
        verify(this.messageService).success("changelog.written", path);
    }

    @Test
    void shouldThrowExceptionWhenWriteFails() throws IOException {
        when(this.writer.write(anyString(), anyBoolean()))
            .thenThrow(new IOException("disk error"));

        assertThatThrownBy(() ->
            this.service.output("content", false, false)
        )
            .isInstanceOf(GitWitException.class)
            .hasMessage(this.i18nService.getMessage("changelog.error.write"));

        verify(this.messageService, never()).success(any(), any());
    }
}
