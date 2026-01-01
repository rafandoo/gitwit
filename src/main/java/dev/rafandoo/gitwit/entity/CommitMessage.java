package dev.rafandoo.gitwit.entity;

import dev.rafandoo.gitwit.cli.wiz.CommitWizard;
import dev.rafandoo.gitwit.config.GitWitConfig;
import dev.rafandoo.gitwit.enums.ChangelogScope;
import dev.rafandoo.gitwit.service.ChangelogService;
import dev.rafandoo.gitwit.service.CommitMessageService;
import dev.rafandoo.cup.utils.StringUtils;
import dev.rafandoo.gitwit.util.EmojiUtil;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Immutable value object representing a Conventional Commit.
 * <pre>
 *   type(scope)!: short description
 *
 *   long description
 *
 *   BREAKING CHANGE: description
 * </pre>
 * Instances are created by {@link CommitWizard} and validated by
 * {@link CommitMessageService}.
 *
 * @param type                commit type, e.g. <code>feat</code>, <code>fix</code>.
 * @param scope               scope affected by the change.
 * @param shortDescription    imperative, present‑tense summary (<code>maxLength</code> enforced).
 * @param longDescription     detailed explanation (can span multiple lines).
 * @param breakingChanges     boolean indicating whether the commit contains breaking changes.
 * @param breakingChangesDesc description of the breaking changes.
 * @param hash                commit hash.
 * @param authorIdent         commit author data as well as the commit date.
 */
public record CommitMessage(
    String type,
    String scope,
    String shortDescription,
    String longDescription,
    boolean breakingChanges,
    String breakingChangesDesc,
    ObjectId hash,
    PersonIdent authorIdent
) {

    /**
     * Formats this message following the Conventional Commits specification.
     *
     * @return formatted commit message ready to be written to <code>commit-msg</code>.
     */
    public String format() {
        StringBuilder sb = new StringBuilder(type.trim());
        if (!StringUtils.isNullOrBlank(scope)) {
            if (EmojiUtil.containsAnyEmojiAlias(type)) {
                sb.append(" ");
            }
            sb.append("(").append(scope.trim()).append(")");
        }
        if (breakingChanges) {
            sb.append("!");
        }
        if (!StringUtils.isNullOrBlank(shortDescription)) {
            sb.append(": ").append(shortDescription.trim());
        }
        if (!StringUtils.isNullOrBlank(longDescription)) {
            sb.append("\n\n").append(longDescription.trim());
        }
        if (!StringUtils.isNullOrBlank(breakingChangesDesc)) {
            sb.append("\n\nBREAKING CHANGE: ").append(breakingChangesDesc.trim());
        }
        return sb.toString();
    }

    /**
     * Formats this commit message as an entry in a changelog.
     *
     * <p>
     * A template is selected based on the provided changelog format and scope,
     * and placeholders are replaced with commit-specific values.
     * </p>
     *
     * @param format changelog format configuration
     * @param scope  scope used to select the appropriate changelog template
     * @return formatted changelog entry
     */
    public String formatForChangelog(GitWitConfig.ChangelogConfig.ChangelogFormat format, ChangelogScope scope) {
        String template = ChangelogService.getInstance().getChangelogCommitTemplateByScope(format, scope);

        String formattedDate = "";
        if (template.contains("{date}") && authorIdent != null) {
            Instant instant = authorIdent.getWhenAsInstant();
            if (instant != null) {
                LocalDateTime dateTime = LocalDateTime.ofInstant(instant, authorIdent.getZoneId());
                formattedDate = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
        }

        return template
            .replace("{type}", StringUtils.isNullOrBlank(this.type) ? "" : this.type.trim())
            .replace("{scope}", StringUtils.isNullOrBlank(this.scope) ? "" : this.scope.trim())
            .replace("{description}", StringUtils.isNullOrBlank(shortDescription) ? "" : shortDescription.trim())
            .replace("{hash}", hash != null && !StringUtils.isNullOrBlank(hash.name()) ? hash.name() : "")
            .replace("{shortHash}", hash != null && !StringUtils.isNullOrBlank(hash.name()) ? hash.abbreviate(Constants.OBJECT_ID_ABBREV_STRING_LENGTH).name() : "")
            .replace("{breakingChanges}", breakingChanges ? "!" : "")
            .replace("{author}", authorIdent == null || StringUtils.isNullOrBlank(authorIdent.getName()) ? "" : authorIdent.getName())
            .replace("{date}", formattedDate)
            .replaceAll("\\s?\\(\\)", "")
            .replaceAll("^:\\s+", "")
            .replaceAll("^\\s+", "");
    }

    /**
     * Internal representation of a parsed commit message.
     * <p>
     * This record exists solely to decouple parsing logic from
     * {@link CommitMessage} construction.
     * </p>
     */
    private record ParsedCommit(
        String type,
        String scope,
        String subject,
        String description,
        boolean breaking,
        String breakingDesc
    ) {
    }

    /**
     * Parses a raw commit message into structured components.
     *
     * @param fullMessage raw commit message
     * @return parsed commit data
     */
    private static ParsedCommit parse(String fullMessage) {
        if (StringUtils.isNullOrBlank(fullMessage)) {
            return new ParsedCommit(null, null, null, null, false, null);
        }

        String[] parts = fullMessage.split("\n\n", 2);
        String header = parts[0];
        String body = parts.length > 1 ? parts[1] : null;

        String type = null;
        String scope = null;
        boolean breaking = false;
        String subject;

        Pattern pattern = Pattern.compile(
            "^(?<type>\\w+|:\\w+:)(?<breaking>!)?\\s?(?:\\((?<scope>[^)]+)\\))?:?\\s*(?<desc>.*)$"
        );

        Matcher matcher = pattern.matcher(header);

        if (matcher.matches()) {
            type = matcher.group("type");
            scope = matcher.group("scope");
            subject = matcher.group("desc");
            breaking = matcher.group("breaking") != null;
        } else {
            subject = header.trim();
        }

        String description = body;
        String breakingDesc = null;

        if (body != null && body.contains("BREAKING CHANGE:")) {
            String[] bodyParts = body.split("(?m)^BREAKING CHANGE:\\s*", 2);
            description = bodyParts[0].trim();
            if (bodyParts.length > 1) {
                breakingDesc = bodyParts[1].trim();
                breaking = true;
            }
        }

        return new ParsedCommit(
            type,
            scope,
            subject,
            description,
            breaking,
            breakingDesc
        );
    }


    /**
     * Creates a {@link CommitMessage} from a {@link RevCommit}.
     *
     * @param commit Git commit to parse
     * @return parsed commit message representation
     */
    public static CommitMessage of(RevCommit commit) {
        if (commit == null || StringUtils.isNullOrBlank(commit.getFullMessage())) {
            return new CommitMessage(
                null,
                null,
                null,
                null,
                false,
                null,
                null,
                null
            );
        }

        ParsedCommit parsed = parse(commit.getFullMessage());

        return new CommitMessage(
            parsed.type(),
            parsed.scope(),
            parsed.subject(),
            parsed.description(),
            parsed.breaking(),
            parsed.breakingDesc(),
            commit.getId(),
            commit.getAuthorIdent()
        );
    }

    /**
     * Creates a {@link CommitMessage} from a raw commit message string.
     *
     * @param rawMessage commit message text
     * @return parsed commit message representation
     */
    public static CommitMessage of(String rawMessage) {
        ParsedCommit parsed = parse(rawMessage);

        return new CommitMessage(
            parsed.type(),
            parsed.scope(),
            parsed.subject(),
            parsed.description(),
            parsed.breaking(),
            parsed.breakingDesc(),
            null,
            null
        );
    }
}
