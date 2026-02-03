package dev.rafandoo.gitwit.exception;

import dev.rafandoo.gitwit.App;
import dev.rafandoo.gitwit.di.InjectorFactory;
import dev.rafandoo.gitwit.service.I18nService;
import dev.rafandoo.gitwit.util.EnvironmentUtil;

import java.io.Serial;

/**
 * Custom runtime exception used within the GitWit application.
 */
public class GitWitException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final I18nService i18nService = InjectorFactory.get().getInstance(I18nService.class);

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
     * Constructs a new GitWitException with the specified error message.
     *
     * @param error the error message.
     */
    public GitWitException(String error) {
        this(error, false);
    }

    /**
     * Constructs a new GitWitException with the specified error message and parameters.
     *
     * @param error  the error message.
     * @param params optional parameters to format the message.
     */
    public GitWitException(String error, String... params) {
        this(error, false, params);
    }

    /**
     * Constructs a new GitWitException with the specified error message and parameters.
     *
     * @param error             the error message.
     * @param suppressClassName if true, suppresses the class name in the exception's string representation.
     * @param params            optional parameters to format the message.
     */
    public GitWitException(String error, boolean suppressClassName, String... params) {
        super(null, null);
        if (!App.isDebug() && !EnvironmentUtil.isTesting()) {
            setStackTrace(new StackTraceElement[0]);
        }
        this.message = this.i18nService.resolve(error, (Object[]) params);
        this.suppressClassName = !App.isDebug() && suppressClassName;
    }

    /**
     * Constructs a new GitWitException with the specified error message and cause.
     *
     * @param error  the error message.
     * @param cause  the cause of this exception.
     * @param params optional parameters to format the message.
     */
    public GitWitException(String error, Throwable cause, String... params) {
        this(error, cause, false, params);
    }

    /**
     * Constructs a new GitWitException with the specified error message, cause and suppression flag.
     *
     * @param error             the error message.
     * @param cause             the cause of this exception.
     * @param suppressClassName if true, suppresses the class name in the exception's string representation.
     * @param params            optional parameters to format the message.
     */
    public GitWitException(String error, Throwable cause, boolean suppressClassName, String... params) {
        super(cause);
        this.message = this.i18nService.resolve(error, (Object[]) params);
        this.suppressClassName = suppressClassName;
    }

    @Override
    public String getMessage() {
        return message != null ? message : this.i18nService.getMessage("error.unspecified");
    }

    @Override
    public String toString() {
        if (suppressClassName) {
            return getLocalizedMessage();
        }
        return super.toString();
    }
}
