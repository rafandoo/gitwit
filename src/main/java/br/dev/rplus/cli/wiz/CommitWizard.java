package br.dev.rplus.cli.wiz;

import br.dev.rplus.cup.utils.StringUtils;
import br.dev.rplus.entity.CommitMessage;
import br.dev.rplus.config.GitWitConfig;
import br.dev.rplus.enums.CommitPromptKeys;
import br.dev.rplus.enums.ExceptionMessage;
import br.dev.rplus.exception.GitWitException;
import br.dev.rplus.service.CommitMessageService;
import br.dev.rplus.service.I18nService;
import br.dev.rplus.service.TerminalService;
import br.dev.rplus.util.EmojiUtil;
import org.jline.consoleui.elements.ConfirmChoice;
import org.jline.consoleui.prompt.ConsolePrompt;
import org.jline.consoleui.prompt.PromptResultItemIF;
import org.jline.consoleui.prompt.builder.ListPromptBuilder;
import org.jline.consoleui.prompt.builder.PromptBuilder;
import org.jline.terminal.Terminal;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Interactive wizard that builds a {@link CommitMessage} based on the rules defined in
 * a {@link GitWitConfig}. The wizard honours all validation constraints (required fields,
 * allowed values, min/max lengths, etc.) before returning the message.
 */
public class CommitWizard {

    private final GitWitConfig config;

    /**
     * Constructs a {@link CommitWizard} with the specified configuration.
     *
     * @param config the {@link GitWitConfig} to be used for commit message generation.
     * @throws GitWitException if the provided configuration is {@code null}.
     */
    public CommitWizard(GitWitConfig config) {
        if (config == null) {
            throw new GitWitException(ExceptionMessage.CONFIGURATION_CANNOT_BE_NULL);
        }
        this.config = config;
    }

    /**
     * Starts the interactive wizard.
     *
     * @return a validated {@link CommitMessage}.
     */
    public CommitMessage run() {
        Terminal terminal = TerminalService.getInstance().getTerminal();

        ConsolePrompt console = new ConsolePrompt(terminal);
        PromptBuilder builder = console.getPromptBuilder();

        /* ─────────── Commit Type ─────────── */
        Map<String, String> types = this.config.getTypes().getValues();
        if (types == null || types.isEmpty()) {
            throw new GitWitException(ExceptionMessage.COMMIT_TYPES_REQUIRED);
        }
        this.promptChoice(
            CommitPromptKeys.COMMIT_TYPE.getKey(),
            this.composePromptMessage(
                I18nService.getInstance().getMessage(CommitPromptKeys.COMMIT_TYPE.getValue()),
                this.config.getTypes().getDescription(),
                false
            ),
            types,
            builder,
            false
        );

        /* ─────────── Commit Scope ─────────── */
        boolean scopeOptional = !this.config.getScope().isRequired();
        String scopePrompt = this.composePromptMessage(
            I18nService.getInstance().getMessage(CommitPromptKeys.COMMIT_SCOPE.getValue()),
            this.config.getScope().getDescription(),
            !this.config.getScope().isRequired()
        );

        switch (this.config.getScope().getType().toLowerCase()) {
            case "list" -> {
                Map<String, String> scopes = this.validateAndCopy(this.config.getScope().getValues(), 12)
                    .keySet()
                    .stream()
                    .collect(Collectors.toMap(Function.identity(), Function.identity()));

                this.promptChoice(
                    CommitPromptKeys.COMMIT_SCOPE.getKey(),
                    scopePrompt,
                    scopes,
                    builder,
                    scopeOptional
                );
            }
            case "text" -> this.promptText(
                CommitPromptKeys.COMMIT_SCOPE.getKey(),
                scopePrompt,
                builder
            );
            default ->
                throw new GitWitException(ExceptionMessage.COMMIT_TYPES_REQUIRED, this.config.getScope().getType());
        }

        /* ─────────── Commit Short Description ─────────── */
        this.promptText(
            CommitPromptKeys.COMMIT_SHORT_DESC.getKey(),
            this.composePromptMessage(
                I18nService.getInstance().getMessage(CommitPromptKeys.COMMIT_SHORT_DESC.getValue()),
                this.config.getShortDescription().getDescription(),
                false
            ),
            builder
        );

        /* ─────────── Commit Long Description ─────────── */
        if (this.config.getLongDescription().isEnabled()) {
            this.promptText(
                CommitPromptKeys.COMMIT_LONG_DESC.getKey(),
                this.composePromptMessage(
                    I18nService.getInstance().getMessage(CommitPromptKeys.COMMIT_LONG_DESC.getValue()),
                    this.config.getLongDescription().getDescription(),
                    !this.config.getLongDescription().isRequired()
                ),
                builder
            );
        }

        /* ─────────── Commit Breaking Changes ─────────── */
        if (this.config.getBreakingChanges().isEnabled()) {
            this.promptConfirm(
                CommitPromptKeys.COMMIT_BREAKING_CHANGES.getKey(),
                this.composePromptMessage(
                    I18nService.getInstance().getMessage(CommitPromptKeys.COMMIT_BREAKING_CHANGES.getValue()),
                    I18nService.getInstance().resolve("commit.prompt.breaking_changes_label"),
                    false
                ),
                builder
            );
        }

        Map<String, PromptResultItemIF> results;
        try {
            results = console.prompt(builder.build());
        } catch (IOException e) {
            throw new GitWitException(ExceptionMessage.COMMIT_WIZARD_CREATION_FAILED, e);
        }

        boolean breakingChanges = false;
        String breakingChoice = this.validatePromptResult(results, CommitPromptKeys.COMMIT_BREAKING_CHANGES.getKey());
        if (!StringUtils.isNullOrEmpty(breakingChoice) && breakingChoice.equalsIgnoreCase(ConfirmChoice.ConfirmationValue.YES.name())) {
            breakingChanges = true;

            results.forEach((k, v) ->
                terminal.writer().println(""));
            terminal.flush();

            PromptBuilder breakingBuilder = console.getPromptBuilder();
            this.createBreakingChangesInputPrompt(breakingBuilder);

            try {
                results.putAll(console.prompt(breakingBuilder.build()));
            } catch (IOException e) {
                throw new GitWitException(ExceptionMessage.COMMIT_WIZARD_CREATION_FAILED, e);
            }
        }

        // Build, validate and return the commit message.
        CommitMessage message = this.buildCommit(results, breakingChanges);
        CommitMessageService.getInstance().validate(message, this.config);

        return message;
    }

    /**
     * Creates a text input prompt for breaking changes description.
     *
     * @param builder the {@link PromptBuilder} to add the breaking changes text prompt to.
     */
    private void createBreakingChangesInputPrompt(PromptBuilder builder) {
        this.promptText(
            CommitPromptKeys.COMMIT_BREAKING_CHANGES_DESC.getKey(),
            this.composePromptMessage(
                I18nService.getInstance().getMessage(CommitPromptKeys.COMMIT_BREAKING_CHANGES_DESC.getValue()),
                this.config.getBreakingChanges().getDescription(),
                false
            ),
            builder
        );
    }

    /**
     * Shows a list prompt with the given choices. Adds an <i>ignore</i> option if the prompt is optional.
     *
     * @param name     the name of the prompt.
     * @param message  the message to show.
     * @param options  the choices.
     * @param builder  the prompt builder.
     * @param optional whether the prompt is optional.
     */
    private void promptChoice(String name, String message, Map<String, String> options, PromptBuilder builder, boolean optional) {
        ListPromptBuilder listPrompt = builder.createListPrompt()
            .name(name)
            .message(message);

        int maxKeyLength = options.keySet()
            .stream()
            .mapToInt(String::length)
            .max()
            .orElse(0);

        options.forEach((key, value) -> listPrompt.newItem()
            .name(key)
            .text(String.format(
                "%-" + maxKeyLength + "s %s",
                EmojiUtil.processEmojis(key),
                EmojiUtil.processEmojis(value)
            ))
            .add());

        if (optional) {
            listPrompt.newItem()
                .name("")
                .text(I18nService.getInstance().getMessage("commit.prompt.ignore"))
                .add();
        }

        listPrompt.addPrompt();
    }

    /**
     * Adds a text input prompt to the prompt builder.
     *
     * @param name    the name of the text prompt.
     * @param message the message to display for the text input.
     * @param builder the prompt builder to add the text prompt to.
     */
    private void promptText(String name, String message, PromptBuilder builder) {
        builder.createInputPrompt()
            .name(name)
            .message(message)
            .addPrompt();
    }

    /**
     * Adds a confirmation prompt to the prompt builder.
     *
     * @param name    the name of the confirmation prompt.
     * @param message the message to display for the confirmation.
     * @param builder the prompt builder to add the confirmation prompt to.
     */
    private void promptConfirm(String name, String message, PromptBuilder builder) {
        builder.createConfirmPromp()
            .name(name)
            .message(message)
            .defaultValue(ConfirmChoice.ConfirmationValue.NO)
            .addPrompt();
    }

    /**
     * Builds a {@link CommitMessage} object from results produced by ConsoleUI.
     *
     * @param results the results of the prompts.
     * @return the {@link CommitMessage} object.
     */
    private CommitMessage buildCommit(Map<String, PromptResultItemIF> results, boolean breakingChanges) {
        return new CommitMessage(
            this.validatePromptResult(results, CommitPromptKeys.COMMIT_TYPE.getKey()),
            this.validatePromptResult(results, CommitPromptKeys.COMMIT_SCOPE.getKey()),
            this.validatePromptResult(results, CommitPromptKeys.COMMIT_SHORT_DESC.getKey()),
            this.validatePromptResult(results, CommitPromptKeys.COMMIT_LONG_DESC.getKey()),
            breakingChanges,
            this.validatePromptResult(results, CommitPromptKeys.COMMIT_BREAKING_CHANGES_DESC.getKey()),
            null,
            null
        );
    }

    /**
     * Validates and retrieves a prompt result from a map of results.
     *
     * @param results the map of prompt results to search.
     * @param key     the key to look up in the results map.
     * @return the result string if present and non-blank, otherwise {@code null}.
     */
    private String validatePromptResult(Map<String, PromptResultItemIF> results, String key) {
        if (results != null && results.containsKey(key)) {
            PromptResultItemIF result = results.get(key);
            if (
                result != null
                    && !StringUtils.isNullOrBlank(result.getResult())
                    && !result.getResult().equalsIgnoreCase("null")
            ) {
                return EmojiUtil.replaceEmojiWithAlias(result.getResult());
            }
        }
        return null;
    }

    /**
     * Safely copies a list of strings into a {@link Map} keyed by the same string, validating null/empty.
     *
     * @param values    the list of strings.
     * @param errorCode the error code to throw if values are null or empty.
     * @return the map of strings.
     * @throws GitWitException if values are null or empty (errorCode passed through)
     */
    private Map<String, String> validateAndCopy(List<String> values, int errorCode) {
        if (values == null || values.isEmpty()) {
            throw new GitWitException(ExceptionMessage.fromCode(errorCode));
        }
        return values.stream().collect(Collectors.toMap(Function.identity(), Function.identity(), (a, b) -> a, HashMap::new));
    }

    /**
     * Formats the text shown to the user, appending optional/mandatory hints.
     *
     * @param name        the name of the prompt.
     * @param description the description of the prompt.
     * @param optional    whether the prompt is optional.
     * @return the formatted message.
     */
    private String composePromptMessage(String name, String description, boolean optional) {
        StringBuilder message = new StringBuilder(name);
        String optionalText = I18nService.getInstance().getMessage("commit.prompt.optional");

        if (!StringUtils.isNullOrBlank(description)) {
            message.append(" (").append(EmojiUtil.processEmojis(description));
            message.append(optional ? ", " + optionalText + "):" : "):");
        } else if (optional) {
            message.append(" (").append(optionalText).append("):");
        } else {
            message.append(":");
        }

        return message.toString();
    }

}
