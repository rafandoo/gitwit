package dev.rafandoo.gitwit.service;

import dev.rafandoo.gitwit.TestUtils;
import dev.rafandoo.gitwit.config.GitWitConfig;
import dev.rafandoo.gitwit.entity.CommitMessage;
import dev.rafandoo.gitwit.exception.GitWitException;
import dev.rafandoo.gitwit.mock.CommitMockFactory;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.jupiter.api.*;
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

    @BeforeEach
    void setup() {
        this.service = new CommitMessageService(this.messageService, this.i18nService);
    }

    Map<String, CommitMessage> mapCommits(List<RevCommit> commits) {
        Map<String, CommitMessage> messages = new HashMap<>();
        commits.forEach(commit -> messages.put(commit.getId().getName(), CommitMessage.of(commit)));
        return messages;
    }

    @Test
    void shouldPassValidationForValidCommits() {
        GitWitConfig config = TestUtils.loadDefaultConfig();

        List<RevCommit> revCommits = List.of(
            CommitMockFactory.mockCommit("abc123", "feat: Add new feature X"),
            CommitMockFactory.mockCommit("def456", "fix: Correct bug in feature Y"),
            CommitMockFactory.mockCommit("ghi789", "docs: Update documentation")
        );
        Map<String, CommitMessage> commits = this.mapCommits(revCommits);

        assertThatNoException().isThrownBy(
            () -> this.service.validate(commits, config)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidCommitProvider")
    void shouldThrowExceptionForInvalidCommits(String id, String message, String expectedKey, Object param) {
        GitWitConfig config = TestUtils.loadDefaultConfig();
        List<RevCommit> revCommits = List.of(
            CommitMockFactory.mockCommit(id, message)
        );
        Map<String, CommitMessage> commits = this.mapCommits(revCommits);

        assertThatThrownBy(() -> this.service.validate(commits, config))
            .isInstanceOf(GitWitException.class)
            .hasMessageContaining(String.format("%040x", id.hashCode()))
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

        List<RevCommit> revCommits = List.of(
            CommitMockFactory.mockCommit("noScope", "feat: No scope")
        );
        Map<String, CommitMessage> commits = this.mapCommits(revCommits);

        assertThatThrownBy(() -> this.service.validate(commits, config))
            .isInstanceOf(GitWitException.class)
            .hasMessageContaining(String.format("%040x", "noScope".hashCode()))
            .hasMessageContaining(
                this.i18nService.getMessage("commit.validation.scope_required")
            );
    }

    @Test
    void shouldThrowExceptionWhenLongDescriptionIsMissing() {
        GitWitConfig config = TestUtils.loadDefaultConfig();
        config.getLongDescription().setRequired(true);

        List<RevCommit> revCommits = List.of(
            CommitMockFactory.mockCommit("noLong", "feat: No long desc")
        );
        Map<String, CommitMessage> commits = this.mapCommits(revCommits);

        assertThatThrownBy(() -> this.service.validate(commits, config))
            .isInstanceOf(GitWitException.class)
            .hasMessageContaining(String.format("%040x", "noLong".hashCode()))
            .hasMessageContaining(
                this.i18nService.getMessage("commit.validation.long_description_required")
            );
    }

    @Test
    void shouldThrowExceptionWhenLongDescriptionIsTooShort() {
        GitWitConfig config = TestUtils.loadDefaultConfig();
        config.getLongDescription().setRequired(true);
        config.getLongDescription().setMinLength(20);

        List<RevCommit> revCommits = List.of(
            CommitMockFactory.mockCommit("shortLong", "feat: Short long desc\n\nToo short")
        );
        Map<String, CommitMessage> commits = this.mapCommits(revCommits);

        assertThatThrownBy(() -> this.service.validate(commits, config))
            .isInstanceOf(GitWitException.class)
            .hasMessageContaining(String.format("%040x", "shortLong".hashCode()))
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

        List<RevCommit> revCommits = List.of(
            CommitMockFactory.mockCommit("longLong", "feat: Long long desc\n\nThis long description is way too long and exceeds the maximum length allowed by the configuration.")
        );
        Map<String, CommitMessage> commits = this.mapCommits(revCommits);

        assertThatThrownBy(() -> this.service.validate(commits, config))
            .isInstanceOf(GitWitException.class)
            .hasMessageContaining(String.format("%040x", "longLong".hashCode()))
            .hasMessageContaining(
                this.i18nService.getMessage(
                    "commit.validation.long_description_too_long",
                    config.getLongDescription().getMaxLength()
                )
            );
    }

    @Nested
    @DisplayName("Commit type validation")
    class CommitTypeValidation {

        @Test
        void shouldFailWhenTypeIsNull() {
            GitWitConfig config = TestUtils.loadDefaultConfig();

            CommitMessage msg = new CommitMessage(
                null,
                null,
                "desc",
                null,
                false,
                null,
                null,
                null
            );

            assertThatThrownBy(() -> service.validate(msg, config))
                .isInstanceOf(GitWitException.class)
                .hasMessageContaining(
                    i18nService.getMessage("commit.validation.invalid_type", "null")
                );
        }
    }

    @Nested
    @DisplayName("Commit scope validation")
    class CommitScopeValidation {

        @Test
        void shouldFailWhenScopeIsNullAndRequired() {
            GitWitConfig config = TestUtils.loadDefaultConfig();
            config.getScope().setRequired(true);

            CommitMessage msg = new CommitMessage(
                "feat",
                null,
                "desc",
                null,
                false,
                null,
                null,
                null
            );

            assertThatThrownBy(() -> service.validate(msg, config))
                .isInstanceOf(GitWitException.class)
                .hasMessageContaining(
                    i18nService.getMessage("commit.validation.scope_required")
                );
        }

        @Test
        void shouldFailWhenScopeIsBlankAndRequired() {
            GitWitConfig config = TestUtils.loadDefaultConfig();
            config.getScope().setRequired(true);

            CommitMessage msg = new CommitMessage(
                "feat",
                "   ",
                "desc",
                null,
                false,
                null,
                null,
                null
            );

            assertThatThrownBy(() -> service.validate(msg, config))
                .isInstanceOf(GitWitException.class)
                .hasMessageContaining(
                    i18nService.getMessage("commit.validation.scope_required")
                );
        }

        @Test
        void shouldPassWhenScopeIsNotRequiredAndNull() {
            GitWitConfig config = TestUtils.loadDefaultConfig();
            config.getScope().setRequired(false);

            CommitMessage msg = new CommitMessage(
                "feat",
                null,
                "description",
                null,
                false,
                null,
                null,
                null
            );

            assertThatNoException()
                .isThrownBy(() -> service.validate(msg, config));
        }
    }

    @Nested
    @DisplayName("Short description length validation")
    class ShortDescriptionLengthValidation {

        @Test
        void shouldFailWhenShortDescriptionIsTooShort() {
            GitWitConfig config = TestUtils.loadDefaultConfig();
            config.getShortDescription().setMinLength(10);

            CommitMessage msg = new CommitMessage(
                "feat",
                null,
                "short",
                null,
                false,
                null,
                null,
                null
            );

            assertThatThrownBy(() -> service.validate(msg, config))
                .isInstanceOf(GitWitException.class)
                .hasMessageContaining(
                    i18nService.getMessage(
                        "commit.validation.short_description_too_short",
                        config.getShortDescription().getMinLength()
                    )
                );
        }

        @Test
        void shouldFailWhenShortDescriptionIsTooLong() {
            GitWitConfig config = TestUtils.loadDefaultConfig();
            config.getShortDescription().setMaxLength(10);

            CommitMessage msg = new CommitMessage(
                "feat",
                null,
                "this description is way too long",
                null,
                false,
                null,
                null,
                null
            );

            assertThatThrownBy(() -> service.validate(msg, config))
                .isInstanceOf(GitWitException.class)
                .hasMessageContaining(
                    i18nService.getMessage(
                        "commit.validation.short_description_too_long",
                        config.getShortDescription().getMaxLength()
                    )
                );
        }
    }
}
