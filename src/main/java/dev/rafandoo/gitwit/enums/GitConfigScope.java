package dev.rafandoo.gitwit.enums;

/**
 * Enum representing the scope of Git configuration.
 */
public enum GitConfigScope {

    /**
     * User-level Git configuration (e.g., ~/.gitconfig).
     */
    GLOBAL,

    /**
     * Repository-level Git configuration (e.g., .git/config).
     */
    LOCAL
}
