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

    public GitWitException(ExceptionMessage error) {
        this(error, false);
    }

    public GitWitException(ExceptionMessage error, String... params) {
        this(error, false, params);
    }

    public GitWitException(ExceptionMessage error, boolean suppressClassName, String... params) {
        super(null, null, false, false);
        this.code = error.getCode();
        this.message = I18nService.getInstance().resolve(error.getMessage(), (Object[]) params);
        this.suppressClassName = suppressClassName;
    }

    public GitWitException(ExceptionMessage error, Throwable cause, String... params) {
        this(error, cause, false, params);
    }

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
