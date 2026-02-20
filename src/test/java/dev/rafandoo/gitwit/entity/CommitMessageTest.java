package dev.rafandoo.gitwit.entity;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.time.ZoneId;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("CommitMessage Tests")
class CommitMessageTest {

    @Nested
    @DisplayName("Parsing commit messages")
    class Parsing {

        @ParameterizedTest
        @MethodSource("conventionalCommitProvider")
        @MethodSource("emojiCommitProvider")
        void shouldParseCorrectlyOnly(
            String raw,
            String expectedType,
            String expectedScope,
            boolean expectedBreaking,
            String expectedShortDescription,
            String expectedLongDescription
        ) {
            CommitMessage msg = CommitMessage.of(raw);

            assertThat(msg)
                .extracting(
                    CommitMessage::type,
                    CommitMessage::scope,
                    CommitMessage::breakingChanges,
                    CommitMessage::shortDescription,
                    CommitMessage::longDescription
                )
                .containsExactly(
                    expectedType,
                    expectedScope,
                    expectedBreaking,
                    expectedShortDescription,
                    expectedLongDescription
                );
        }

        static Stream<Arguments> conventionalCommitProvider() {
            return Stream.of(
                Arguments.of(
                    "fix: correct minor typos in code",
                    "fix",
                    null,
                    false,
                    "correct minor typos in code",
                    null
                ),
                Arguments.of(
                    "feat(core): add new engine",
                    "feat",
                    "core",
                    false,
                    "add new engine",
                    null
                ),
                Arguments.of(
                    "feat(api)!: change response format",
                    "feat",
                    "api",
                    true,
                    "change response format",
                    null
                ),
                Arguments.of(
                    "docs: update README\n",
                    "docs",
                    null,
                    false,
                    "update README",
                    null
                ),
                Arguments.of(
                    """
                        chore: update dependencies

                        Updated all dependencies to their latest versions.""",
                    "chore",
                    null,
                    false,
                    "update dependencies",
                    "Updated all dependencies to their latest versions."
                ),
                Arguments.of(
                    "feat: ",
                    "feat",
                    null,
                    false,
                    null,
                    null
                )
            );
        }

        static Stream<Arguments> emojiCommitProvider() {
            return Stream.of(
                Arguments.of(
                    ":sparkles: add new feature",
                    ":sparkles:",
                    null,
                    false,
                    "add new feature",
                    null
                ),
                Arguments.of(
                    ":sparkles:(core): add engine",
                    ":sparkles:",
                    "core",
                    false,
                    "add engine",
                    null
                ),
                Arguments.of(
                    ":bug:(api)!: fix critical issue",
                    ":bug:",
                    "api",
                    true,
                    "fix critical issue",
                    null
                ),
                Arguments.of(
                    """
                        :fire:(config): remove deprecated property

                        Cleanup unused configuration""",
                    ":fire:",
                    "config",
                    false,
                    "remove deprecated property",
                    "Cleanup unused configuration"
                ),
                Arguments.of(
                    """
                        :boom:(auth)!: change login flow

                        BREAKING CHANGE: token format updated
                        """,
                    ":boom:",
                    "auth",
                    true,
                    "change login flow",
                    null
                ),
                Arguments.of(
                    ":bug: (:fire:): fix issue with config",
                    ":bug:",
                    ":fire:",
                    false,
                    "fix issue with config",
                    null
                )
            );
        }

        @Test
        void shouldParseFromRawString() {
            String raw = """
                feat(core): add support

                Some long description

                BREAKING CHANGE: config format changed
                """;

            CommitMessage msg = CommitMessage.of(raw);

            assertThat(msg)
                .extracting(
                    CommitMessage::type,
                    CommitMessage::scope,
                    CommitMessage::shortDescription,
                    CommitMessage::longDescription,
                    CommitMessage::breakingChanges,
                    CommitMessage::breakingChangesDesc
                )
                .containsExactly(
                    "feat",
                    "core",
                    "add support",
                    "Some long description",
                    true,
                    "config format changed"
                );
        }

        @Test
        void shouldHandleInvalidCommitGracefully() {
            CommitMessage msg = CommitMessage.of("");

            assertThat(msg)
                .extracting(
                    CommitMessage::type,
                    CommitMessage::scope,
                    CommitMessage::shortDescription,
                    CommitMessage::longDescription,
                    CommitMessage::breakingChanges
                )
                .containsExactly(
                    null,
                    null,
                    null,
                    null,
                    false
                );
        }

        @Test
        void shouldCreateFromRevCommit() {
            RevCommit commit = mock(RevCommit.class);
            ObjectId id = ObjectId.fromString("0123456789012345678901234567890123456789");

            PersonIdent author = new PersonIdent(
                "Rafa",
                "rafa@email.com",
                Instant.parse("2025-01-01T10:15:30Z"),
                ZoneId.of("UTC")
            );

            when(commit.getFullMessage())
                .thenReturn("fix(cli): correct output");
            when(commit.getId())
                .thenReturn(id);
            when(commit.getAuthorIdent())
                .thenReturn(author);

            CommitMessage msg = CommitMessage.of(commit);

            assertThat(msg)
                .extracting(
                    CommitMessage::type,
                    CommitMessage::scope,
                    CommitMessage::shortDescription,
                    CommitMessage::hash,
                    CommitMessage::authorIdent
                )
                .containsExactly(
                    "fix",
                    "cli",
                    "correct output",
                    id,
                    author
                );
        }

        @Test
        void shouldHandleBreakingChangeWithoutDescription() {
            String raw = """
                feat!: change behavior

                BREAKING CHANGE:
                """;

            CommitMessage msg = CommitMessage.of(raw);

            assertThat(msg.breakingChanges()).isTrue();
            assertThat(msg.breakingChangesDesc()).isNull();
        }

        @Test
        void shouldReturnEmptyCommitMessageWhenRevCommitIsNull() {
            CommitMessage msg = CommitMessage.of((RevCommit) null);

            assertThat(msg)
                .extracting(
                    CommitMessage::type,
                    CommitMessage::scope,
                    CommitMessage::shortDescription,
                    CommitMessage::longDescription,
                    CommitMessage::breakingChanges
                )
                .containsExactly(
                    null,
                    null,
                    null,
                    null,
                    false
                );
        }

        @Test
        void shouldHandleBreakingChangeMarkerWithoutDescription() {
            CommitMessage msg = CommitMessage.of("""
                feat: change

                BREAKING CHANGE:
                """);

            assertThat(msg.breakingChanges()).isTrue();
            assertThat(msg.breakingChangesDesc()).isNull();
        }

        @Test
        void shouldTreatEmptyScopeAsNull() {
            CommitMessage msg = CommitMessage.of("feat (): add feature");

            assertThat(msg.scope()).isNull();
            assertThat(msg.format()).isEqualTo("feat: add feature");
        }

        @Test
        void shouldFallbackWhenHeaderDoesNotMatchPattern() {
            CommitMessage msg = CommitMessage.of(".");

            assertThat(msg.shortDescription())
                .isEqualTo(".");
        }

        @Test
        void shouldHandleRevCommitWithEmptyMessage() {
            RevCommit commit = mock(RevCommit.class);
            when(commit.getFullMessage()).thenReturn("");

            CommitMessage msg = CommitMessage.of(commit);

            assertThat(msg.type()).isNull();
            assertThat(msg.breakingChanges()).isFalse();
        }
    }

    @Nested
    @DisplayName("Formatting commit messages")
    class Formatting {

        @Test
        void shouldFormatBreakingChangeCommit() {
            CommitMessage msg = new CommitMessage(
                "feat",
                "api",
                "change endpoint",
                null,
                true,
                "endpoint renamed",
                null,
                null
            );

            assertThat(msg.format())
                .isEqualTo(
                    """
                        feat(api)!: change endpoint

                        BREAKING CHANGE: endpoint renamed"""
                );
        }

        @Test
        void shouldFormatWithLongDescription() {
            CommitMessage msg = new CommitMessage(
                "fix",
                null,
                "handle null pointer",
                "This fixes an edge case\nwhen value is null",
                false,
                null,
                null,
                null
            );

            assertThat(msg.format())
                .isEqualTo(
                    """
                        fix: handle null pointer

                        This fixes an edge case
                        when value is null"""
                );
        }

        @Test
        void shouldIgnoreEmptyLongDescription() {
            CommitMessage msg = new CommitMessage(
                "fix",
                null,
                "handle case",
                "",
                false,
                null,
                null,
                null
            );

            assertThat(msg.format())
                .isEqualTo("fix: handle case");
        }

        @Test
        void shouldAddSpaceBetweenEmojiTypeAndScope() {
            CommitMessage msg = new CommitMessage(
                ":sparkles:",
                "core",
                "add feature",
                null,
                false,
                null,
                null,
                null
            );

            assertThat(msg.format())
                .isEqualTo(":sparkles: (core): add feature");
        }

        @Test
        void shouldNotAppendColonWhenShortDescriptionIsNull() {
            CommitMessage msg = new CommitMessage(
                "feat",
                null,
                null,
                null,
                false,
                null,
                null,
                null
            );

            assertThat(msg.format()).isEqualTo("feat");
        }
    }

    @Nested
    @DisplayName("Formatting commit messages for changelog")
    class FormattingForChangelog {

        @Test
        void shouldFormatForChangelogWithAllFields() {
            ObjectId id = ObjectId.fromString("0123456789012345678901234567890123456789");

            PersonIdent author = new PersonIdent(
                "Rafa",
                "rafa@email.com",
                Instant.parse("2025-01-01T10:15:30Z"),
                ZoneId.of("UTC")
            );

            CommitMessage msg = new CommitMessage(
                "feat",
                "core",
                "add engine",
                null,
                true,
                null,
                id,
                author
            );

            String template = "- {type}({scope}){breakingChanges}: {description} [{shortHash}] by {author} on {date}";

            String result = msg.formatForChangelog(template);

            assertThat(result)
                .contains("feat(core)!")
                .contains("add engine")
                .contains("by Rafa")
                .contains("2025-01-01");
        }

        @Test
        void shouldRemoveEmptyScopeInChangelog() {
            CommitMessage msg = new CommitMessage(
                "fix",
                null,
                "minor fix",
                null,
                false,
                null,
                null,
                null
            );

            String template = "- {type}({scope}): {description}";

            String result = msg.formatForChangelog(template);

            assertThat(result)
                .isEqualTo("- fix: minor fix");
        }

        @Test
        void shouldReturnEmptyDateWhenAuthorIsNull() {
            CommitMessage msg = new CommitMessage(
                "fix",
                null,
                "bug",
                null,
                false,
                null,
                null,
                null
            );

            String result = msg.formatForChangelog("{date}");

            assertThat(result).isEmpty();
        }

        @Test
        void shouldHandleNullInstantInAuthorIdent() {
            PersonIdent author = mock(PersonIdent.class);
            when(author.getWhenAsInstant()).thenReturn(null);

            CommitMessage msg = new CommitMessage(
                "fix",
                null,
                "bug",
                null,
                false,
                null,
                null,
                author
            );

            assertThat(msg.formatForChangelog("{date}")).isEmpty();
        }

        @Test
        void shouldHandleNullTypeInChangelog() {
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

            assertThat(msg.formatForChangelog("{type}:{description}"))
                .isEqualTo(":desc");
        }

        @Test
        void shouldHandleNullDescriptionInChangelog() {
            CommitMessage msg = new CommitMessage(
                "fix",
                null,
                null,
                null,
                false,
                null,
                null,
                null
            );

            assertThat(msg.formatForChangelog("{type}: {description}"))
                .isEqualTo("fix: ");
        }
    }
}
