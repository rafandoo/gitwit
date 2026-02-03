package dev.rafandoo.gitwit.service;

import dev.rafandoo.gitwit.config.GitWitConfig;
import dev.rafandoo.cup.utils.StringUtils;
import dev.rafandoo.gitwit.entity.CommitMessage;
import dev.rafandoo.gitwit.entity.Violation;
import dev.rafandoo.gitwit.enums.CommitPromptKeys;
import dev.rafandoo.gitwit.exception.GitWitException;
import dev.rafandoo.gitwit.util.EmojiUtil;

import java.util.*;

/**
 * Class responsible for validating a {@link CommitMessage} against the rules
 * declared in {@link GitWitConfig}. All validation errors are mapped to
 * {@link GitWitException}s with specific error codes so they can be handled by the CLI.
 */
public final class CommitMessageService {

    private static CommitMessageService instance;

    /**
     * Map of error messages for each error code.
     */
    private static final Map<Integer, String> MESSAGES = new HashMap<>() {{
        put(1, "commit.validation.invalid_type");
        put(2, "commit.validation.type_not_allowed");
        put(3, "commit.validation.scope_required");
        put(4, "commit.validation.short_description_required");
        put(5, "commit.validation.short_description_too_short");
        put(6, "commit.validation.short_description_too_long");
        put(7, "commit.validation.long_description_required");
        put(8, "commit.validation.long_description_too_short");
        put(9, "commit.validation.long_description_too_long");
    }};

    /**
     * Private constructor to prevent instantiation.
     */
    private CommitMessageService() {
    }

    /**
     * Returns the singleton instance, instantiating it on first use.
     *
     * @return {@link CommitMessageService} instance.
     */
    public static synchronized CommitMessageService getInstance() {
        if (instance == null) {
            instance = new CommitMessageService();
        }
        return instance;
    }

    /**
     * Collects all validation violations for a given commit message
     * against the provided configuration.
     *
     * @param message the message to validate.
     * @param config  the configuration to use.
     * @return list of {@link Violation}s found during validation.
     */
    public List<Violation> collectViolations(CommitMessage message, GitWitConfig config) {
        List<Violation> violations = new ArrayList<>();

        /* ─────────── Commit Type ─────────── */
        this.ensure(
            !StringUtils.isNullOrBlank(message.type()),
            1,
            CommitPromptKeys.COMMIT_TYPE,
            message.type()
        ).ifPresent(violations::add);
        this.ensure(
            config.getTypes()
                .getValues()
                .keySet()
                .stream()
                .map(EmojiUtil::replaceEmojiWithAlias)
                .toList()
                .contains(message.type()),
            2,
            CommitPromptKeys.COMMIT_TYPE,
            message.type()
        ).ifPresent(violations::add);

        /* ─────────── Commit Scope ─────────── */
        this.ensure(
            !(config.getScope().isRequired() && StringUtils.isNullOrBlank(message.scope())),
            3,
            CommitPromptKeys.COMMIT_SCOPE
        ).ifPresent(violations::add);

        /* ─────────── Commit Short Description ─────────── */
        this.ensure(
            !StringUtils.isNullOrBlank(message.shortDescription()),
            4,
            CommitPromptKeys.COMMIT_SHORT_DESC
        ).ifPresent(violations::add);

        if (message.shortDescription() != null) {
            int shortMin = config.getShortDescription().getMinLength();
            int shortMax = config.getShortDescription().getMaxLength();

            this.ensure(
                message.shortDescription().length() >= shortMin,
                5,
                CommitPromptKeys.COMMIT_SHORT_DESC,
                shortMin
            ).ifPresent(violations::add);
            this.ensure(
                message.shortDescription().length() <= shortMax,
                6,
                CommitPromptKeys.COMMIT_SHORT_DESC,
                shortMax
            ).ifPresent(violations::add);
        }

        /* ─────────── Commit Long Description ─────────── */
        if (config.getLongDescription().isRequired()) {
            this.ensure(
                !StringUtils.isNullOrBlank(message.longDescription()),
                7,
                CommitPromptKeys.COMMIT_LONG_DESC
            ).ifPresent(violations::add);

            if (message.longDescription() != null) {
                int longMin = config.getLongDescription().getMinLength();
                int longMax = config.getLongDescription().getMaxLength();

                this.ensure(
                    message.longDescription().length() >= longMin,
                    8,
                    CommitPromptKeys.COMMIT_LONG_DESC,
                    longMin
                ).ifPresent(violations::add);
                this.ensure(
                    message.longDescription().length() <= longMax,
                    9,
                    CommitPromptKeys.COMMIT_LONG_DESC,
                    longMax
                ).ifPresent(violations::add);
            }
        }
        return violations;
    }

    /**
     * Performs all validation rules and throws {@link GitWitException} on failures.
     *
     * @param message the message to validate.
     * @param config  the configuration to use.
     * @throws GitWitException if any validation fails.
     */
    public void validate(CommitMessage message, GitWitConfig config) {
        List<Violation> violations = this.collectViolations(message, config);

        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append(I18nService.getInstance().getMessage("commit.validation.violations"))
                .append(":\n");
            for (Violation violation : violations) {
                sb.append(" - ").append(violation).append("\n");
            }

            throw new GitWitException(
                MessageService.getInstance().getErrorMessage(sb.toString()).toAnsi(),
                true
            );
        }
    }

    /**
     * Validates a list of commit messages against the given configuration.
     *
     * @param messages list of commit messages to validate.
     * @param config   the configuration to use.
     * @throws GitWitException if any validation fails.
     */
    public void validate(Map<String, CommitMessage> messages, GitWitConfig config) {
        Map<String, List<Violation>> allViolations = new HashMap<>();

        messages.forEach((key, message) -> {
            List<Violation> violations = this.collectViolations(message, config);
            if (!violations.isEmpty()) {
                allViolations.put(key, new ArrayList<>(violations));
            }
        });

        if (!allViolations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append(I18nService.getInstance().getMessage("commit.validation.violations"))
                .append(":\n");
            allViolations.forEach((key, violations) -> {
                sb.append(" - ").append(key).append(":\n");
                violations.forEach(violation -> sb.append("    - ").append(violation).append("\n"));
            });
            throw new GitWitException(
                MessageService.getInstance().getErrorMessage(sb.toString()).toAnsi(),
                true
            );
        }
    }

    /**
     * Ensures a specific condition is met for a commit message validation.
     *
     * @param condition the validation condition to check.
     * @param code      the error code or identifier for the validation rule.
     * @param scope     the scope of the validation.
     * @param params    optional parameters to include in the violation message.
     * @return an {@link Optional} containing the {@link Violation} if the condition is not met,
     * or an empty {@link Optional} if the condition is met.
     */
    private Optional<Violation> ensure(boolean condition, int code, CommitPromptKeys scope, Object... params) {
        if (!condition) {
            return Optional.of(
                Violation.of(
                    I18nService.getInstance().resolve(scope.getValue()),
                    I18nService.getInstance().resolve(MESSAGES.get(code), params)
                )
            );
        }
        return Optional.empty();
    }
}
