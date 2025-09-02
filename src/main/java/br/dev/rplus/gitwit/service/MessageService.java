package br.dev.rplus.gitwit.service;

import br.dev.rplus.gitwit.App;
import br.dev.rplus.gitwit.enums.TerminalStyle;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

/**
 * Service for handling terminal message logging.
 * Provides methods for printing and formatting messages with
 * different styles and levels (error, warn, info, debug, success).
 */
public final class MessageService {

    private static MessageService instance;

    /**
     * Private constructor to prevent instantiation.
     */
    private MessageService() {
    }

    /**
     * Returns the singleton instance, instantiating it on first use.
     *
     * @return {@link MessageService} instance.
     */
    public static synchronized MessageService getInstance() {
        if (instance == null) {
            instance = new MessageService();
        }
        return instance;
    }

    /**
     * Creates and returns a new {@link AttributedStringBuilder} instance.
     *
     * @return A new {@link AttributedStringBuilder} ready for use.
     */
    private AttributedStringBuilder getBuilder() {
        return new AttributedStringBuilder();
    }

    /**
     * Prints an attributed string message to the terminal.
     *
     * @param message the attributed string message to be printed.
     */
    private void print(AttributedString message) {
        Terminal terminal = TerminalService.getInstance().getTerminal();
        terminal.writer().println(message.toAnsi());
        terminal.flush();
    }

    /**
     * Creates an attributed string message with a specific type and style.
     *
     * @param type    the key of type of message (e.g., "ERROR", "WARN", "INFO").
     * @param style   the {@link AttributedStyle} to apply to the message type.
     * @param message the message template to format.
     * @param params  optional parameters to format into the message.
     * @return an {@link AttributedString} with the formatted and styled message.
     */
    public AttributedString getMessage(String type, AttributedStyle style, String message, Object... params) {
        return this.getBuilder()
            .style(style)
            .append(this.getMessage(type)).append(": ")
            .style(AttributedStyle.DEFAULT)
            .append(this.getMessage(message, params))
            .toAttributedString();
    }

    /**
     * Creates an attributed string message for error logging with a specific style.
     *
     * @param message the error message template to format.
     * @param params  optional parameters to format into the error message.
     * @return an {@link AttributedString} with the formatted and styled error message.
     */
    public AttributedString getErrorMessage(String message, Object... params) {
        return this.getMessage("message.error", TerminalStyle.ERROR.asAttributedStyle(), message, params);
    }

    /**
     * Prints an error message to the terminal with a specific style.
     *
     * @param message the error message template to format.
     * @param params  optional parameters to format into the error message.
     */
    public void error(String message, Object... params) {
        this.print(
            this.getErrorMessage(message, params)
        );
    }

    /**
     * Prints a warning message to the terminal with a specific style.
     *
     * @param message the warning message template to format.
     * @param params  optional parameters to format into the warning message.
     */
    public void warn(String message, Object... params) {
        this.print(
            this.getBuilder()
                .style(TerminalStyle.WARN.asAttributedStyle())
                .append(this.getMessage("message.warning"))
                .append(": ")
                .style(AttributedStyle.DEFAULT)
                .append(this.getMessage(message, params))
                .toAttributedString()
        );
    }

    /**
     * Prints an informational message to the terminal with a specific style.
     *
     * @param message the informational message template to format.
     * @param params  optional parameters to format into the message.
     */
    public void info(String message, Object... params) {
        this.print(
            this.getBuilder()
                .style(TerminalStyle.INFO.asAttributedStyle())
                .append(this.getMessage("message.info"))
                .append(": ")
                .style(AttributedStyle.DEFAULT)
                .append(this.getMessage(message, params))
                .toAttributedString()
        );
    }

    /**
     * Prints a debug message to the terminal with a specific style, only when debug mode is enabled.
     *
     * @param message the debug message template to format.
     * @param params  optional parameters to format into the debug message.
     */
    public void debug(String message, Object... params) {
        if (App.isDebug()) {
            this.print(
                this.getBuilder()
                    .style(TerminalStyle.INFO.asAttributedStyle())
                    .append(this.getMessage("message.debug"))
                    .append(": ")
                    .style(AttributedStyle.DEFAULT)
                    .append(this.getMessage(message, params))
                    .toAttributedString()
            );
        }
    }

    /**
     * Prints a success message to the terminal with a specific style.
     *
     * @param message the success message template to format.
     * @param params  optional parameters to format into the success message.
     */
    public void success(String message, Object... params) {
        this.print(
            this.getBuilder()
                .style(TerminalStyle.SUCCESS.asAttributedStyle())
                .append(this.getMessage("message.success"))
                .append(": ")
                .style(AttributedStyle.DEFAULT)
                .append(this.getMessage(message, params))
                .toAttributedString()
        );
    }

    /**
     * Retrieves a formatted message using the I18n service with optional parameters.
     *
     * @param message the message key or template to be localized.
     * @param params  optional parameters to format into the message.
     * @return the localized and formatted message.
     */
    private String getMessage(String message, Object... params) {
        return I18nService.getInstance().resolve(message, params);
    }
}
