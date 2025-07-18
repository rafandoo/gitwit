package br.dev.rplus.entity;

/**
 * Immutable representation of a single lint violation produced during commit‑message validation.
 * <p>
 * Each {@code Violation} is composed of:
 * <ul>
 *   <li>{@code scope} – a logical category or rule identifier that triggered the violation
 *   (e.g. {@code "shortDescription"}, {@code "type"}).</li>
 *   <li>{@code message} – a human‑readable explanation formatted for display to the user.</li>
 * </ul>
 *
 * @param scope   logical rule or subsystem where the violation originated.
 * @param message detailed description of the problem.
 */
public record Violation(String scope, String message) {

    /**
     * Creates a new Violation instance with the given scope and message.
     *
     * @param scope   the scope or subsystem where the violation occurred.
     * @param message the message describing the violation.
     * @return a new {@link Violation} instance.
     */
    public static Violation of(String scope, String message) {
        return new Violation(scope, message);
    }

    @Override
    public String toString() {
        return this.scope + ": " + this.message;
    }
}
