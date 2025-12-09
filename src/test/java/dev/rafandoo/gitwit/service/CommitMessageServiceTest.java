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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CommitMessageService Tests")
class CommitMessageServiceTest {

    private List<RevCommit> commits;

    private GitWitConfig loadDefaultConfig() {
        TestUtils.setupConfig(".general.gitwit");
        return GitWitConfig.load();
    }

    private Map<String, CommitMessage> mapCommits(List<RevCommit> commits) {
        Map<String, CommitMessage> messages = new HashMap<>();
        commits.forEach(commit -> messages.put(commit.getId().getName(), CommitMessage.of(commit)));
        return messages;
    }

    private void putCommit(Map<String, CommitMessage> map, String id, String message) {
        map.put(id, CommitMessage.of(CommitMockFactory.mockCommit(id, message)));
    }

    @BeforeEach
    void setupCommits() {
        this.commits = List.of(
            CommitMockFactory.mockCommit("abc123", "feat: Add new feature X"),
            CommitMockFactory.mockCommit("def456", "fix: Correct bug in feature Y"),
            CommitMockFactory.mockCommit("ghi789", "docs: Update documentation")
        );
    }

    @Test
    void shouldPassValidationForValidCommits() {
        GitWitConfig config = this.loadDefaultConfig();
        Map<String, CommitMessage> messages = this.mapCommits(this.commits);
        assertDoesNotThrow(() -> CommitMessageService.getInstance().validate(messages, config));
    }

    @ParameterizedTest
    @MethodSource("invalidCommitProvider")
    void shouldThrowExceptionForInvalidCommits(String id, String message, String expectedKey) {
        GitWitConfig config = this.loadDefaultConfig();
        Map<String, CommitMessage> messages = this.mapCommits(this.commits);
        this.putCommit(messages, id, message);

        GitWitException ex = assertThrows(
            GitWitException.class,
            () -> CommitMessageService.getInstance().validate(messages, config)
        );

        String expected = I18nService.getInstance().getMessage(expectedKey);
        assertAll(
            () -> assertTrue(ex.getMessage().contains(id)),
            () -> assertTrue(ex.getMessage().contains(expected))
        );
    }

    private static Stream<Arguments> invalidCommitProvider() {
        return Stream.of(
            Arguments.of("invalid", "invalid commit message", "commit.validation.violations"),
            Arguments.of("type", "abc: Not allowed", "commit.validation.type_not_allowed"),
            Arguments.of("noShort", "feat:", "commit.validation.short_description_required")
        );
    }

    @Test
    void shouldThrowExceptionWhenScopeIsMissing() {
        GitWitConfig config = this.loadDefaultConfig();
        config.getScope().setRequired(true);

        Map<String, CommitMessage> messages = this.mapCommits(this.commits);
        this.putCommit(messages, "noScope", "feat: No scope");

        GitWitException ex = assertThrows(
            GitWitException.class,
            () -> CommitMessageService.getInstance().validate(messages, config)
        );

        String expected = I18nService.getInstance().getMessage("commit.validation.scope_required");
        assertAll(
            () -> assertTrue(ex.getMessage().contains("noScope")),
            () -> assertTrue(ex.getMessage().contains(expected))
        );
    }

    @Test
    void shouldThrowExceptionWhenLongDescriptionIsMissing() {
        GitWitConfig config = this.loadDefaultConfig();
        config.getLongDescription().setRequired(true);

        Map<String, CommitMessage> messages = this.mapCommits(this.commits);
        this.putCommit(messages, "noLong", "feat: No long desc");

        GitWitException ex = assertThrows(
            GitWitException.class,
            () -> CommitMessageService.getInstance().validate(messages, config)
        );

        String expected = I18nService.getInstance().getMessage("commit.validation.long_description_required");
        assertAll(
            () -> assertTrue(ex.getMessage().contains("noLong")),
            () -> assertTrue(ex.getMessage().contains(expected))
        );
    }

    @Test
    void shouldThrowExceptionWhenLongDescriptionIsTooShort() {
        GitWitConfig config = this.loadDefaultConfig();
        config.getLongDescription().setRequired(true);
        config.getLongDescription().setMinLength(20);

        Map<String, CommitMessage> messages = this.mapCommits(this.commits);
        this.putCommit(messages, "shortLong", "feat: Short long desc\n\nToo short");

        GitWitException ex = assertThrows(
            GitWitException.class,
            () -> CommitMessageService.getInstance().validate(messages, config)
        );

        String expected = I18nService.getInstance().getMessage(
            "commit.validation.long_description_too_short",
            config.getLongDescription().getMinLength()
        );
        assertAll(
            () -> assertTrue(ex.getMessage().contains("shortLong")),
            () -> assertTrue(ex.getMessage().contains(expected))
        );
    }

    @Test
    void shouldThrowExceptionWhenLongDescriptionIsTooLong() {
        GitWitConfig config = this.loadDefaultConfig();
        config.getLongDescription().setRequired(true);
        config.getLongDescription().setMaxLength(50);

        Map<String, CommitMessage> messages = this.mapCommits(this.commits);
        this.putCommit(messages, "longLong",
            "feat: Very long long desc\n\nThis long description is way too long and exceeds the maximum length allowed by the configuration."
        );

        GitWitException ex = assertThrows(
            GitWitException.class,
            () -> CommitMessageService.getInstance().validate(messages, config)
        );

        String expected = I18nService.getInstance().getMessage(
            "commit.validation.long_description_too_long",
            config.getLongDescription().getMaxLength()
        );
        assertAll(
            () -> assertTrue(ex.getMessage().contains("longLong")),
            () -> assertTrue(ex.getMessage().contains(expected))
        );
    }

}
