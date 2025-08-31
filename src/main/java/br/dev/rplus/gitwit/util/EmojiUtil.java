package br.dev.rplus.gitwit.util;

import lombok.experimental.UtilityClass;
import net.fellbaum.jemoji.Emoji;
import net.fellbaum.jemoji.EmojiManager;

import java.util.regex.Pattern;

/**
 * Utility class for handling emoji-related operations.
 */
@UtilityClass
public final class EmojiUtil {

    /**
     * Regular expression pattern to match emoji aliases in the format {@code :alias:}.
     */
    private static final Pattern ALIAS_PATTERN = Pattern.compile(":(\\w+):");

    /**
     * Checks if the given text contains any emoji alias.
     *
     * @param text the text to check.
     * @return {@code true} if the text contains at least one emoji alias, {@code false} otherwise.
     */
    public static boolean containsAnyEmojiAlias(String text) {
        return ALIAS_PATTERN.matcher(text).find();
    }

    /**
     * Processes the given text by replacing emoji aliases with their corresponding emojis.
     *
     * @param text the text to process.
     * @return the processed text with emoji aliases replaced by emojis.
     */
    public static String processEmojis(String text) {
        if (containsAnyEmojiAlias(text)) {
            return EmojiManager.replaceAliases(
                text,
                (alias, emojis) -> emojis.stream()
                    .filter(emoji -> emoji.getGithubAliases().contains(alias))
                    .findAny()
                    .map(Emoji::getEmoji)
                    .orElse(alias)
            );
        }

        return text;
    }

    /**
     * Replaces all emojis in the given text with their first GitHub alias.
     *
     * @param text the text to process.
     * @return the text with emojis replaced by their aliases.
     */
    public static String replaceEmojiWithAlias(String text) {
        if (EmojiManager.containsAnyEmoji(text)) {
            return EmojiManager.replaceAllEmojis(
                text,
                emoji -> emoji.getGithubAliases().getFirst()
            );
        }
        return text;
    }
}
