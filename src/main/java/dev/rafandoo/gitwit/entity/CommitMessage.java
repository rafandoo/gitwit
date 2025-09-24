package dev.rafandoo.gitwit.entity;

import dev.rafandoo.gitwit.cli.wiz.CommitWizard;
import dev.rafandoo.gitwit.config.GitWitConfig;
import dev.rafandoo.gitwit.enums.ChangelogScope;
import dev.rafandoo.gitwit.service.ChangelogService;
import dev.rafandoo.gitwit.service.CommitMessageService;
import br.dev.rplus.cup.utils.StringUtils;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        StringBuilder sb = new StringBuilder(type);
        if (!StringUtils.isNullOrBlank(scope)) {
            sb.append(" (").append(scope).append(")");
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
     * Formats the commit message for a changelog entry.
     * <p>
     * This method uses a template based on the provided format and scope,
     * replacing placeholders with the commit's details.
     *
     * @param format the configuration for the changelog format.
     * @param scope  the scope of the changelog entry, which determines the template used.
     * @return a formatted string suitable for changelog entries.
     */
    public String formatForChangelog(GitWitConfig.ChangelogConfig.ChangelogFormat format, ChangelogScope scope) {
        String template = ChangelogService.getInstance().getChangelogCommitTemplateByScope(format, scope);

        String formattedDate = "";
        if (authorIdent != null && authorIdent.getWhenAsInstant() != null) {
            Instant instant = authorIdent.getWhenAsInstant();
            LocalDateTime dateTime = LocalDateTime.ofInstant(instant, authorIdent.getZoneId());
            formattedDate = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
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
     * Creates a {@link CommitMessage} from a {@link RevCommit} by parsing its full message.
     * <p>
     * Parses the commit message into type, optional scope, subject, and optional body
     * according to the Conventional Commits specification. Handles various message formats,
     * including malformed messages.
     *
     * @param commit thw commit to parse.
     * @return a new CommitMessage instance, or a null-filled CommitMessage if the input is invalid.
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

        String[] parts = commit.getFullMessage().split("\n\n", 2);
        String header = parts[0];
        String body = parts.length > 1 ? parts[1] : null;

        // header = "type(scope): subject"  OR  "type: subject"
        String type = "";
        String scope = null;
        boolean breakingChange = false;
        String subject;

        int colon = header.lastIndexOf(':');
        if (colon < 0) {                            // malformed, keep everything as subject
            subject = header.trim();
        } else {
            String headLeft = header.substring(0, colon).trim();   // type or type(scope)
            subject = header.substring(colon + 1).trim();

            breakingChange = headLeft.endsWith("!");
            if (breakingChange) {
                headLeft = header.substring(0, headLeft.length() - 1);
            }

            int open = headLeft.indexOf('(');
            int close = headLeft.indexOf(')');
            if (open > 0 && close > open) {
                type = headLeft.substring(0, open);
                scope = headLeft.substring(open + 1, close);
            } else {
                type = headLeft;
            }
        }

        String description = body;
        String breakingChangeDesc = null;

        if (body != null && body.contains("BREAKING CHANGE:")) {
            String[] bodyParts = body.split("(?m)^BREAKING CHANGE:\\s*", 2);
            description = bodyParts[0].trim();
            if (bodyParts.length > 1) {
                breakingChangeDesc = bodyParts[1].trim();
                breakingChange = true;
            }
        }

        return new CommitMessage(
            type.trim(),
            scope,
            subject,
            description,
            breakingChange,
            breakingChangeDesc,
            commit.getId(),
            commit.getAuthorIdent()
        );
    }
}
