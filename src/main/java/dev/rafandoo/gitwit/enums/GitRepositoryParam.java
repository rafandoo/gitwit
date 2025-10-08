package dev.rafandoo.gitwit.enums;

import dev.rafandoo.cup.enums.TypedValue;

/**
 * Represents configuration parameters and constants for Git repository settings.
 */
public enum GitRepositoryParam {

    /**
     * Name of the custom hooks directory relative to the repository root.
     */
    HOOKS_DIR_NAME(".githooks"),

    /**
     * Name of the commit wizard hook file.
     */
    PREPARE_COMMIT_MSG("prepare-commit-msg"),

    /**
     * Default editor for Git.
     */
    CORE_EDITOR_DEFAULT("vi"),

    /**
     * Default editor for GitWit.
     */
    CORE_EDITOR_CAT("cat"),

    /**
     * Name of the Git alias for GitWit.
     */
    GITWIT_ALIAS("wit");

    private final TypedValue value;

    GitRepositoryParam(Object value) {
        this.value = new TypedValue(value);
    }

    public TypedValue get() {
        return this.value;
    }
}
