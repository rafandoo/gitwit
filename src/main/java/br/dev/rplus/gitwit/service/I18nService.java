package br.dev.rplus.gitwit.service;

import br.dev.rplus.gitwit.enums.ConfigPaths;
import lombok.Getter;
import br.dev.rplus.gitwit.util.EmojiUtil;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Internationalization (I18n) service that provides localized message retrieval.
 */
public final class I18nService {

    private static I18nService instance;

    @Getter
    private final ResourceBundle messages;

    /**
     * Private constructor to prevent instantiation.
     */
    private I18nService() {
        this.messages = ResourceBundle.getBundle(
            ConfigPaths.MESSAGES_FILE.get().asString(),
            Locale.getDefault()
        );
    }

    /**
     * Returns the singleton instance, instantiating it on first use.
     *
     * @return {@link I18nService} instance.
     */
    public static synchronized I18nService getInstance() {
        if (instance == null) {
            instance = new I18nService();
        }
        return instance;
    }

    /**
     * Retrieves a localized message for the given key from the resource bundle.
     *
     * @param key the message key to look up.
     * @return the localized message corresponding to the key.
     */
    public String getMessage(String key) {
        String message = this.messages.getString(key);
        return EmojiUtil.processEmojis(message);
    }

    /**
     * Retrieves a localized message for the given key and formats it with the provided parameters.
     *
     * @param key    the message key to look up.
     * @param params optional parameters to format the message using {@link MessageFormat}.
     * @return the localized message corresponding to the key, with parameters substituted.
     */
    public String getMessage(String key, Object... params) {
        return MessageFormat.format(this.getMessage(key), params);
    }

    /**
     * Resolves the input string as a message key if it exists in the bundle;
     * otherwise, treats it as a plain format string.
     *
     * @param message the message key or plain string.
     * @param params  optional parameters to format.
     * @return localized or formatted message.
     */
    public String resolve(String message, Object... params) {
        if (this.messages.containsKey(message)) {
            return this.getMessage(message, params);
        }
        return EmojiUtil.processEmojis(String.format(message, params));
    }
}
