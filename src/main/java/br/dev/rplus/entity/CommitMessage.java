package br.dev.rplus.entity;

import br.dev.rplus.cli.wiz.CommitWizard;
import br.dev.rplus.service.CommitMessageService;
import br.dev.rplus.cup.utils.StringUtils;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.revwalk.RevCommit;

/**
 * Immutable value object representing a Conventional Commit.
 * <pre>
 *   type(scope): short description
 *
 *   long description
 * </pre>
 * Instances are created by {@link CommitWizard} and validated by
 * {@link CommitMessageService}.
 *
 * @param type             commit type, e.g. <code>feat</code>, <code>fix</code>.
 * @param scope            scope affected by the change.
 * @param shortDescription imperative, present‑tense summary (<code>maxLength</code> enforced).
 * @param longDescription  detailed explanation (can span multiple lines).
 * @param hash             commit hash.
 */
public record CommitMessage(String type, String scope, String shortDescription, String longDescription, String hash) {

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
        if (!StringUtils.isNullOrBlank(shortDescription)) {
            sb.append(": ").append(shortDescription.trim());
        }
        if (!StringUtils.isNullOrBlank(longDescription)) {
            sb.append("\n\n").append(longDescription.trim());
        }
        return sb.toString();
    }

    /**
     * Formats the commit message for a changelog entry.
     * <p>
     * Generates a changelog-specific format that includes the optional scope,
     * short description, and commit hash in parentheses.
     *
     * @return a formatted string suitable for changelog entries.
     */
    public String formatForChangelog() {
        StringBuilder sb = new StringBuilder();
        if (!StringUtils.isNullOrBlank(scope)) {
            sb.append(scope).append(": ");
        }
        sb.append(shortDescription.trim());
        sb.append(" (").append(hash.trim()).append(")");
        return sb.toString();
    }

    /**
     * Formats the commit message for a changelog entry with additional details.
     * <p>
     * Similar to {@link #formatForChangelog()}, but includes the commit type
     * and optionally the scope in the output.
     *
     * @return a formatted string suitable for alternative changelog entries.
     */
    public String formatForChangelogOthers() {
        StringBuilder sb = new StringBuilder(type);
        if (!StringUtils.isNullOrBlank(scope)) {
            sb.append(" (").append(scope).append(")");
        }
        sb.append(": ").append(shortDescription.trim());
        sb.append(" (").append(hash.trim()).append(")");
        return sb.toString();
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
            return new CommitMessage(null, null, null, null, null);
        }

        String[] parts = commit.getFullMessage().split("\n\n", 2);
        String header = parts[0];
        String body = parts.length > 1 ? parts[1] : null;

        // header = "type(scope): subject"  OR  "type: subject"
        String type;
        String scope = null;
        String subject;

        int colon = header.indexOf(':');
        if (colon < 0) {                            // malformed, keep everything as subject
            type = "";
            subject = header.trim();
        } else {
            String headLeft = header.substring(0, colon).trim();   // type or type(scope)
            subject = header.substring(colon + 1).trim();

            int open = headLeft.indexOf('(');
            int close = headLeft.indexOf(')');
            if (open > 0 && close > open) {
                type = headLeft.substring(0, open);
                scope = headLeft.substring(open + 1, close);
            } else {
                type = headLeft;
            }
        }
        return new CommitMessage(
            type.trim(),
            scope,
            subject,
            body,
            commit.getId().abbreviate(Constants.OBJECT_ID_ABBREV_STRING_LENGTH).name()
        );
    }
}
