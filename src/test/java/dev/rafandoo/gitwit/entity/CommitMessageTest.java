package dev.rafandoo.gitwit.entity;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("CommitMessage Tests")
class CommitMessageTest {

    @Test
    void shouldFormatSimpleCommit() {
        CommitMessage msg = new CommitMessage(
            "feat",
            "core",
            "add new feature",
            null,
            false,
            null,
            null,
            null
        );

        assertThat(msg.format())
            .isEqualTo("feat(core): add new feature");
    }

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
    void shouldParseFromRawString() {
        String raw = """
            feat(core): add support

            Some long description

            BREAKING CHANGE: config format changed
            """;

        CommitMessage msg = CommitMessage.of(raw);

        assertThat(msg.type())
            .isEqualTo("feat");
        assertThat(msg.scope())
            .isEqualTo("core");
        assertThat(msg.shortDescription())
            .isEqualTo("add support");
        assertThat(msg.longDescription())
            .isEqualTo("Some long description");
        assertThat(msg.breakingChanges())
            .isTrue();
        assertThat(msg.breakingChangesDesc())
            .isEqualTo("config format changed");
    }

    @Test
    void shouldHandleInvalidCommitGracefully() {
        CommitMessage msg = CommitMessage.of("");

        assertThat(msg.type())
            .isNull();
        assertThat(msg.shortDescription())
            .isNull();
        assertThat(msg.breakingChanges())
            .isFalse();
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

        assertThat(msg.type())
            .isEqualTo("fix");
        assertThat(msg.scope())
            .isEqualTo("cli");
        assertThat(msg.shortDescription())
            .isEqualTo("correct output");
        assertThat(msg.hash())
            .isEqualTo(id);
        assertThat(msg.authorIdent())
            .isEqualTo(author);
    }

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
}
