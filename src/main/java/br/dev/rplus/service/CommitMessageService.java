package br.dev.rplus.service;

import br.dev.rplus.config.GitWitConfig;
import br.dev.rplus.cup.utils.StringUtils;
import br.dev.rplus.entity.CommitMessage;
import br.dev.rplus.entity.Violation;
import br.dev.rplus.enums.CommitPromptKeys;
import br.dev.rplus.enums.ExceptionMessage;
import br.dev.rplus.exception.GitWitException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        put(2, "commit.validation.not_allowed_type");
        put(3, "commit.validation.missing_scope");
        put(4, "commit.validation.missing_short_description");
        put(5, "commit.validation.short_description_too_short");
        put(6, "commit.validation.short_description_too_long");
        put(7, "commit.validation.missing_long_description");
        put(8, "commit.validation.long_description_too_short");
        put(9, "commit.validation.long_description_too_long");
    }};
    private List<Violation> violations;

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
     * Performs all validation rules and throws {@link GitWitException} on failures.
     *
     * @param message the message to validate.
     * @param config  the configuration to use.
     * @throws GitWitException if any validation fails.
     */
    public void validate(CommitMessage message, GitWitConfig config) {
        this.validate(message, config, true);
    }

    /**
     * Performs all validation rules and throws {@link GitWitException} on failures.
     *
     * @param message        the message to validate.
     * @param config         the configuration to use.
     * @param throwOnFailure if true, throws {@link GitWitException} on failure.
     * @throws GitWitException if any validation fails.
     */
    public void validate(CommitMessage message, GitWitConfig config, boolean throwOnFailure) {
        this.violations = new ArrayList<>();

        /* ─────────── Commit Type ─────────── */
        this.ensure(
            !StringUtils.isNullOrBlank(message.type()),
            1,
            CommitPromptKeys.COMMIT_TYPE
        );
        this.ensure(
            config.getTypes().getValues().containsKey(message.type()),
            2,
            CommitPromptKeys.COMMIT_TYPE
        );

        /* ─────────── Commit Scope ─────────── */
        this.ensure(
            !(config.getScope().isRequired() && StringUtils.isNullOrBlank(message.scope())),
            3,
            CommitPromptKeys.COMMIT_SCOPE
        );

        /* ─────────── Commit Short Description ─────────── */
        if (config.getShortDescription().isRequired()) {
            this.ensure(
                !StringUtils.isNullOrBlank(message.shortDescription()),
                4,
                CommitPromptKeys.COMMIT_SHORT_DESC
            );

            int shortMin = config.getShortDescription().getMinLength();
            int shortMax = config.getShortDescription().getMaxLength();

            this.ensure(
                message.shortDescription().length() >= shortMin,
                5,
                CommitPromptKeys.COMMIT_SHORT_DESC,
                shortMin
            );
            this.ensure(
                message.shortDescription().length() <= shortMax,
                6,
                CommitPromptKeys.COMMIT_SHORT_DESC,
                shortMax
            );
        }

        /* ─────────── Commit Long Description ─────────── */
        if (config.getLongDescription().isRequired()) {
            int longMin = config.getLongDescription().getMinLength();
            int longMax = config.getLongDescription().getMaxLength();

            this.ensure(
                !StringUtils.isNullOrBlank(message.longDescription()),
                7,
                CommitPromptKeys.COMMIT_LONG_DESC
            );

            this.ensure(
                message.longDescription().length() >= longMin,
                8,
                CommitPromptKeys.COMMIT_LONG_DESC,
                longMin
            );
            this.ensure(
                message.longDescription().length() <= longMax,
                9,
                CommitPromptKeys.COMMIT_LONG_DESC,
                longMax
            );
        }

        if (!this.violations.isEmpty() && throwOnFailure) {
            StringBuilder sb = new StringBuilder();
            sb.append(I18nService.getInstance().getMessage("commit.validation.violations"))
                .append(":\n");
            for (Violation violation : this.violations) {
                sb.append(" - ").append(violation).append("\n");
            }

            throw new GitWitException(
                ExceptionMessage.GENERAL,
                true,
                MessageService.getInstance().getErrorMessage(sb.toString()).toAnsi()
            );
        }
    }

    /**
     * Validates a list of commit messages against the given configuration.
     *
     * @param messages list of commit messages to validate.
     * @param config   the configuration to use.
     */
    public void validate(Map<String, CommitMessage> messages, GitWitConfig config) {
        Map<String, List<Violation>> allViolations = new HashMap<>();

        messages.forEach((key, message) -> {
            this.violations = new ArrayList<>();

            this.validate(message, config, false);
            if (!this.violations.isEmpty()) {
                allViolations.put(key, new ArrayList<>(this.violations));
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
                ExceptionMessage.GENERAL,
                true,
                MessageService.getInstance().getErrorMessage(sb.toString()).toAnsi()
            );
        }
    }

    /**
     * Ensures a specific condition is met for a commit message validation.
     * If the condition is not met, a violation is added to the {@link #violations} list.
     *
     * @param condition the validation condition to check.
     * @param code      the error code or identifier for the validation rule.
     * @param scope     the scope of the validation.
     * @param params    optional parameters to include in the violation message.
     */
    private void ensure(boolean condition, int code, CommitPromptKeys scope, Object... params) {
        if (!condition) {
            this.violations.add(Violation.of(
                I18nService.getInstance().getMessage(scope.getValue()),
                I18nService.getInstance().getMessage(MESSAGES.get(code), params)
            ));
        }
    }
}
