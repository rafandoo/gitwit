package br.dev.rplus.exception;

import br.dev.rplus.enums.ExceptionMessage;
import br.dev.rplus.service.I18nService;
import lombok.Getter;

import java.io.Serial;

/**
 * Custom runtime exception used within the GitWit application.
 */
public class GitWitException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Stores the unique error code associated with this exception.
     */
    @Getter
    private final int code;

    /**
     * Stores the localized error message for this exception.
     */
    private final String message;

    /**
     * Flag to suppress the class name in the exception's string representation.
     * When true, the exception will only display the localized message without the class name.
     */
    private final boolean suppressClassName;

    /**
     * Constructs a new GitWitException with the specified error code and message.
     *
     * @param error the error message enum containing the code and message.
     */
    public GitWitException(ExceptionMessage error) {
        this(error, false);
    }

    /**
     * Constructs a new GitWitException with the specified error code, message, and parameters.
     *
     * @param error  the error message enum containing the code and message.
     * @param params optional parameters to format the message.
     */
    public GitWitException(ExceptionMessage error, String... params) {
        this(error, false, params);
    }

    /**
     * Constructs a new GitWitException with the specified error code, message, and parameters.
     *
     * @param error             the error message enum containing the code and message.
     * @param suppressClassName if true, suppresses the class name in the exception's string representation.
     * @param params            optional parameters to format the message.
     */
    public GitWitException(ExceptionMessage error, boolean suppressClassName, String... params) {
        super(null, null, false, false);
        this.code = error.getCode();
        this.message = I18nService.getInstance().resolve(error.getMessage(), (Object[]) params);
        this.suppressClassName = suppressClassName;
    }

    /**
     * Constructs a new GitWitException with the specified error code, message, and cause.
     *
     * @param error  the error message enum containing the code and message.
     * @param cause  the cause of this exception.
     * @param params optional parameters to format the message.
     */
    public GitWitException(ExceptionMessage error, Throwable cause, String... params) {
        this(error, cause, false, params);
    }

    /**
     * Constructs a new GitWitException with the specified error code, message, cause, and suppression flag.
     *
     * @param error             the error message enum containing the code and message.
     * @param cause             the cause of this exception.
     * @param suppressClassName if true, suppresses the class name in the exception's string representation.
     * @param params            optional parameters to format the message.
     */
    public GitWitException(ExceptionMessage error, Throwable cause, boolean suppressClassName, String... params) {
        super(cause);
        this.code = error.getCode();
        this.message = I18nService.getInstance().resolve(error.getMessage(), (Object[]) params);
        this.suppressClassName = suppressClassName;
    }

    @Override
    public String getMessage() {
        return message != null ? message : I18nService.getInstance().getMessage("error.not_specified");
    }

    @Override
    public String toString() {
        if (suppressClassName) {
            return getLocalizedMessage();
        }
        return super.toString();
    }
}
