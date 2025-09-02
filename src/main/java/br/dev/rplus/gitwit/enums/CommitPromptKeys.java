package br.dev.rplus.gitwit.enums;

import lombok.Getter;

/**
 * Represents the keys used for commit prompt configurations.
 */
@Getter
public enum CommitPromptKeys {

    COMMIT_TYPE("commit.type", "commit.prompt.type"),
    COMMIT_SCOPE("commit.scope", "commit.prompt.scope"),
    COMMIT_SHORT_DESC("commit.short_desc", "commit.prompt.short_description"),
    COMMIT_BREAKING_CHANGES("commit.breaking_changes", "commit.prompt.breaking_changes"),
    COMMIT_BREAKING_CHANGES_DESC("commit.breaking_changes_desc", "commit.prompt.breaking_changes_description"),
    COMMIT_LONG_DESC("commit.long_desc", "commit.prompt.long_description");

    private final String key;
    private final String value;

    CommitPromptKeys(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
