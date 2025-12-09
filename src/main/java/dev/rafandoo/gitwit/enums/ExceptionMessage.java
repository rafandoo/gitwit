package dev.rafandoo.gitwit.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * Represents a collection of predefined exception messages with unique codes for error handling
 * in the application. Each enum constant corresponds to a specific error scenario with an
 * associated numeric code and a message key for internationalization.
 */
@Getter
public enum ExceptionMessage {

    GENERAL(1, "error.general"),
    APP_DIRECTORY_ERROR(31, "error.app_dir"),
    CONFIG_FILE_NOT_FOUND(32, "config.error.not_found"),
    CONFIG_FILE_INVALID(33, "config.error.invalid"),
    CONFIG_EXAMPLE_COPY_FAILED(33, "config.error.copy_example"),
    CONFIG_EXAMPLE_NOT_FOUND(34, "config.error.example_missing"),
    COMMIT_WIZARD_CREATION_FAILED(2, "commit.wizard.error.creation"),
    COMMIT_MSG_WRITE_FAILED(3, "commit.hook.error.commit_write"),
    COMMIT_EXECUTION_FAILED(7, "commit.failure"),
    TERMINAL_CREATION_ERROR(4, "terminal.error.create"),
    TERMINAL_FINISH_ERROR(5, "terminal.error.finish"),
    CONFIGURATION_CANNOT_BE_NULL(6, "config.error.null"),

    COMMIT_TYPES_REQUIRED(10, "commit.wizard.error.commit_types_required"),
    SCOPE_TYPE_INVALID(11, "commit.wizard.error.scope_invalid"),
    SCOPE_VALUES_REQUIRED(12, "commit.wizard.error.scope_values_required"),
    CHANGELOG_TYPES_REQUIRED(13, "changelog.error.types_required"),
    CHANGELOG_FAILURE_WRITE(14, "changelog.error.write"),
    CHANGELOG_NO_TEMPLATE_DEFINED(16, "changelog.error.no_template"),
    CLIPBOARD_COPY_FAILURE(15, "clipboard.error.copy"),

    NOT_A_GIT_REPOSITORY(21, "git.error.not_a_repo"),
    INIT_REPOSITORY_FAILED(26, "git.error.init_failed"),
    DEFAULT_HOOKS_MOVE_FAILED(22, "git.hook.error.move_default_hooks"),
    HOOK_MOVE_FAILED(23, "git.hook.error.move_failed"),
    PREPARE_HOOK_WRITE_FAILED(24, "git.hook.error.hook_write"),
    CORE_HOOK_PATH_FAILED(25, "git.hook.error.error.core_hook_path"),

    NO_HEAD(26, "git.repo.error.no_head"),
    UNMERGED_PATHS(27, "git.repo.error.unmerged"),
    WRONG_REPOSITORY_STATE(28, "git.repo.error.invalid_state"),
    SERVICE_UNAVAILABLE(29, "git.error.unavailable"),
    CONCURRENT_REF_UPDATE(30, "git.repo.error.concurrent_update"),
    ABORTED_BY_HOOK(31, "git.repo.error.aborted_by_hook"),
    NO_COMMIT_MESSAGE(32, "git.error.commit.no_message"),
    EMPTY_COMMIT(33, "git.error.commit.empty"),
    GIT_API_EXCEPTION(34, "git.error.api_exception"),
    GIT_CONFIG_INVALID(35, "git.error.config_invalid"),
    UNSUPPORTED_OBJECT_TYPE(36, "git.error.unsupported"),
    MISSING_OBJECT(37, "git.repo.error.missing_object"),
    REV_SPEC_NOT_FOUND(38, "git.repo.error.rev_not_found")
    ;

    private final int code;
    private final String message;

    ExceptionMessage(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * Retrieves an ExceptionMessage enum constant based on its numeric code.
     *
     * @param code the numeric code to match against an ExceptionMessage.
     * @return the matching ExceptionMessage, or null if no match is found.
     */
    public static ExceptionMessage fromCode(int code) {
        return Arrays.stream(values())
            .filter(msg -> msg.code == code)
            .findFirst()
            .orElse(null);
    }
}
