package dev.rafandoo.gitwit.service;

import com.google.inject.Singleton;
import dev.rafandoo.gitwit.enums.ConfigPaths;
import lombok.Getter;
import dev.rafandoo.gitwit.util.EmojiUtil;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Internationalization (I18n) service that provides localized message retrieval.
 */
@Singleton
public final class I18nService {

    @Getter
    private final ResourceBundle messages;

    public I18nService() {
        this.messages = ResourceBundle.getBundle(
            ConfigPaths.MESSAGES_FILE.get().asString(),
            Locale.getDefault()
        );
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
