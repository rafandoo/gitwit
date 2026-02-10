package dev.rafandoo.gitwit.service;

import dev.rafandoo.gitwit.TestUtils;
import dev.rafandoo.gitwit.config.GitWitConfig;
import dev.rafandoo.gitwit.entity.CommitMessage;
import dev.rafandoo.gitwit.exception.GitWitException;
import dev.rafandoo.gitwit.mock.CommitMockFactory;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommitMessageService Tests")
class CommitMessageServiceTest {

    @Mock
    TerminalService terminalService;

    CommitMessageService service;

    I18nService i18nService = new I18nService();

    @Spy
    MessageService messageService = new MessageService(terminalService, i18nService);

    List<RevCommit> commits;

    @BeforeEach
    void setup() {
        this.service = new CommitMessageService(this.messageService, this.i18nService);
        this.commits = List.of(
            CommitMockFactory.mockCommit("abc123", "feat: Add new feature X"),
            CommitMockFactory.mockCommit("def456", "fix: Correct bug in feature Y"),
            CommitMockFactory.mockCommit("ghi789", "docs: Update documentation")
        );
    }

    Map<String, CommitMessage> mapCommits(List<RevCommit> commits) {
        Map<String, CommitMessage> messages = new HashMap<>();
        commits.forEach(commit -> messages.put(commit.getId().getName(), CommitMessage.of(commit)));
        return messages;
    }

    void putCommit(Map<String, CommitMessage> map, String id, String message) {
        map.put(id, CommitMessage.of(CommitMockFactory.mockCommit(id, message)));
    }

    @Test
    void shouldPassValidationForValidCommits() {
        GitWitConfig config = TestUtils.loadDefaultConfig();
        Map<String, CommitMessage> messages = this.mapCommits(this.commits);
        assertThatNoException().isThrownBy(
            () -> this.service.validate(messages, config)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidCommitProvider")
    void shouldThrowExceptionForInvalidCommits(String id, String message, String expectedKey, Object param) {
        GitWitConfig config = TestUtils.loadDefaultConfig();
        Map<String, CommitMessage> messages = this.mapCommits(this.commits);
        this.putCommit(messages, id, message);

        assertThatThrownBy(() -> this.service.validate(messages, config))
            .isInstanceOf(GitWitException.class)
            .hasMessageContaining(id)
            .hasMessageContaining(this.i18nService.resolve(expectedKey, param));
    }

    private static Stream<Arguments> invalidCommitProvider() {
        return Stream.of(
            Arguments.of("invalid", "invalid commit message", "commit.validation.violations", null),
            Arguments.of("type", "abc: Not allowed", "commit.validation.type_not_allowed", "abc"),
            Arguments.of("noShort", "feat:", "commit.validation.short_description_required", null)
        );
    }

    @Test
    void shouldThrowExceptionWhenScopeIsMissing() {
        GitWitConfig config = TestUtils.loadDefaultConfig();
        config.getScope().setRequired(true);

        Map<String, CommitMessage> messages = this.mapCommits(this.commits);
        this.putCommit(messages, "noScope", "feat: No scope");

        assertThatThrownBy(() -> this.service.validate(messages, config))
            .isInstanceOf(GitWitException.class)
            .hasMessageContaining("noScope")
            .hasMessageContaining(
                this.i18nService.getMessage("commit.validation.scope_required")
            );
    }

    @Test
    void shouldThrowExceptionWhenLongDescriptionIsMissing() {
        GitWitConfig config = TestUtils.loadDefaultConfig();
        config.getLongDescription().setRequired(true);

        Map<String, CommitMessage> messages = this.mapCommits(this.commits);
        this.putCommit(messages, "noLong", "feat: No long desc");

        assertThatThrownBy(() -> this.service.validate(messages, config))
            .isInstanceOf(GitWitException.class)
            .hasMessageContaining("noLong")
            .hasMessageContaining(
                this.i18nService.getMessage("commit.validation.long_description_required")
            );
    }

    @Test
    void shouldThrowExceptionWhenLongDescriptionIsTooShort() {
        GitWitConfig config = TestUtils.loadDefaultConfig();
        config.getLongDescription().setRequired(true);
        config.getLongDescription().setMinLength(20);

        Map<String, CommitMessage> messages = this.mapCommits(this.commits);
        this.putCommit(messages, "shortLong", "feat: Short long desc\n\nToo short");

        assertThatThrownBy(() -> this.service.validate(messages, config))
            .isInstanceOf(GitWitException.class)
            .hasMessageContaining("shortLong")
            .hasMessageContaining(
                this.i18nService.getMessage(
                    "commit.validation.long_description_too_short",
                    config.getLongDescription().getMinLength()
                )
            );
    }

    @Test
    void shouldThrowExceptionWhenLongDescriptionIsTooLong() {
        GitWitConfig config = TestUtils.loadDefaultConfig();
        config.getLongDescription().setRequired(true);
        config.getLongDescription().setMaxLength(50);

        Map<String, CommitMessage> messages = this.mapCommits(this.commits);
        this.putCommit(messages, "longLong",
            "feat: Very long long desc\n\nThis long description is way too long and exceeds the maximum length allowed by the configuration."
        );

        assertThatThrownBy(() -> this.service.validate(messages, config))
            .isInstanceOf(GitWitException.class)
            .hasMessageContaining("longLong")
            .hasMessageContaining(
                this.i18nService.getMessage(
                    "commit.validation.long_description_too_long",
                    config.getLongDescription().getMaxLength()
                )
            );
    }
}
