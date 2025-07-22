package br.dev.rplus.enums;

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
    APP_DIRECTORY_ERROR(31, "error.app_directory_error"),
    CONFIG_FILE_NOT_FOUND(32, "error.config_file_not_found"),
    CONFIG_FILE_INVALID(33, "error.config_file_invalid"),
    CONFIG_EXAMPLE_COPY_FAILED(33, "error.config_example_copy_failed"),
    COMMIT_WIZARD_CREATION_FAILED(2, "error.commit_wizard_creation_failed"),
    COMMIT_MSG_WRITE_FAILED(3, "error.commit_msg_write_failed"),
    TERMINAL_CREATION_ERROR(4, "error.terminal_creation_error"),
    TERMINAL_FINISH_ERROR(5, "error.terminal_finish_error"),
    CONFIGURATION_CANNOT_BE_NULL(6, "error.configuration_cannot_be_null"),

    COMMIT_TYPES_REQUIRED(10, "error.commit_types_required"),
    SCOPE_TYPE_INVALID(11, "error.scope_type_invalid"),
    SCOPE_VALUES_REQUIRED(12, "error.scope_values_required"),
     CHANGELOG_TYPES_REQUIRED(13, "error.changelog_types_required"),
    CHANGELOG_FAILURE_WRITE(14, "error.changelog_failure_write"),
    CLIPBOARD_COPY_FAILURE(15, "error.clipboard_copy_failure"),

    NOT_A_GIT_REPOSITORY(21, "error.not_a_git_repository"),
    INIT_REPOSITORY_FAILED(26, "error.init_repository_failed"),
    DEFAULT_HOOKS_MOVE_FAILED(22, "error.default_hooks_move_failed"),
    HOOK_MOVE_FAILED(23, "error.hook_move_failed"),
    PREPARE_HOOK_WRITE_FAILED(24, "error.prepare_hook_write_failed"),
    CORE_HOOK_PATH_FAILED(25, "error.core_hook_path_failed"),

    NO_HEAD(26, "error.git.no_head"),
    UNMERGED_PATHS(27, "error.git.unmerged_paths"),
    WRONG_REPOSITORY_STATE(28, "error.git.wrong_repository_state"),
    SERVICE_UNAVAILABLE(29, "error.git.service_unavailable"),
    CONCURRENT_REF_UPDATE(30, "error.git.concurrent_ref_update"),
    ABORTED_BY_HOOK(31, "error.git.aborted_by_hook"),
    NO_COMMIT_MESSAGE(32, "error.git.no_commit_message"),
    EMPTY_COMMIT(33, "error.git.empty_commit"),
    GIT_API_EXCEPTION(34, "error.git.git_api_exception"),
    GIT_CONFIG_INVALID(35, "error.git.git_config_invalid"),
    UNSUPPORTED_OBJECT_TYPE(36, "error.git.unsupported_object_type");

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
