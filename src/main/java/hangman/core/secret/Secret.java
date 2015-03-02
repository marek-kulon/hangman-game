package hangman.core.secret;

import hangman.core.guess.Guess;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Stream;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

public final class Secret implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Space separator between words */
    public static final char SPACE_SEPARATOR = ' ';

    /** Unmodifiable set of allowed characters */
    public static final Set<Character> ALLOWED_CHARACTERS;
    static {
        Set<Character> alphas = new HashSet<>();
        "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".chars().forEach(ci -> alphas.add((char) ci));
        ALLOWED_CHARACTERS = Collections.unmodifiableSet(alphas);
    }

    private final String value;
    private final Category category;

    // do not expose - use factory methods instead
    private Secret(String value, Category category) {
        notBlank(value);
        notNull(category);

        this.value = value;
        this.category = category;
    }

    /**
     * Returns a {@code Secret} of a specified not-blank value and not-null category
     *
     * @param value    value to guess
     * @param category category of secret
     * @return newly created {@code Secret}
     */
    public static Secret of(String value, Category category) {
        return new Secret(value, category);
    }

    public String getValue() {
        return value;
    }

    public Category getCategory() {
        return category;
    }

    public static boolean isValidSecretCharacter(char value) {
        return value == SPACE_SEPARATOR || ALLOWED_CHARACTERS.contains(value);
    }

    /**
     * @return how many guess does it take to know the secret
     */
    public long getGuessesToKnowMeNo() {
        return getValue().chars()
                .filter(c -> Guess.isValidGuessCharacter((char) c)) // space separators
                .mapToObj(c -> Guess.of((char) c))
                .distinct()
                .count();
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (obj.getClass() != getClass()) return false;

        Secret other = (Secret) obj;
        return Objects.equals(this.category, other.category) && Objects.equals(this.value, other.value);
    }

    @Override
    public String toString() {
        return String.format("Secret [value=%s, category=%s]", value, category);
    }


    /**
     * Available categories of secrets.
     */
    public static enum Category {
        ANIMALS, FRUITS, VEGETABLES;

        /**
         * Finds {@code Category} by its name ignoring case considerations.
         *
         * @param value the {@code String} to compare to name of {@code Category}
         */
        public static Optional<Category> getByNameIgnoreCase(final String value) {
            notNull(value);

            return Stream.of(Category.values())
                    .filter(category -> category.name().equalsIgnoreCase(value))
                    .findAny();
        }
    }
}
