package dev.rafandoo.gitwit.enums;

import dev.rafandoo.cup.enums.TypedValue;
import org.jline.utils.AttributedStyle;

/**
 * Represents different terminal text styles for displaying messages with varying visual emphasis.
 * <p>
 * This enum provides predefined styles for error, warning, info, and success messages,
 * each with a corresponding JLine {@link AttributedStyle} configuration.
 */
public enum TerminalStyle {

    /**
     * The error style.
     */
    ERROR(AttributedStyle.BOLD.foreground(AttributedStyle.RED)),

    /**
     * The warning style.
     */
    WARN(AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW)),

    /**
     * The info style.
     */
    INFO(AttributedStyle.DEFAULT.foreground(AttributedStyle.BLUE)),

    /**
     * The success style.
     */
    SUCCESS(AttributedStyle.BOLD.foreground(AttributedStyle.GREEN));

    private final TypedValue value;

    TerminalStyle(Object value) {
        this.value = new TypedValue(value);
    }

    public TypedValue get() {
        return this.value;
    }

    /**
     * Converts the terminal style's value to an {@link AttributedStyle} instance.
     *
     * @return the {@link AttributedStyle} representation of this terminal style
     */
    public AttributedStyle asAttributedStyle() {
        return this.value.getTypedValue(AttributedStyle.class);
    }
}
