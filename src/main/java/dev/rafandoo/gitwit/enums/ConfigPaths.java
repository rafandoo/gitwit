package dev.rafandoo.gitwit.enums;

import dev.rafandoo.cup.enums.TypedValue;

/**
 * Enumeration of configuration file paths and related settings.
 */
public enum ConfigPaths {

    CONFIG_FILE(".gitwit"),
    EXAMPLE_CONFIG_FILE("example_%s.gitwit"),

    CHANGELOG_FILE("CHANGELOG.md"),

    MESSAGES_FILE("i18n.messages");

    private final TypedValue value;

    ConfigPaths(Object value) {
        this.value = new TypedValue(value);
    }

    public TypedValue get() {
        return this.value;
    }
}
