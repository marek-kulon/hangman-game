package hangman.core.secret;

import hangman.core.guess.Guess;
import org.apache.commons.lang3.Validate;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Stream;

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

    public String getValue() {
        return value;
    }

    public Category getCategory() {
        return category;
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

    public static boolean isValidSecretCharacter(char value) {
        return value == SPACE_SEPARATOR || ALLOWED_CHARACTERS.contains(value);
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

    // private constructor
    private Secret(String value, Category category) {
        Validate.notBlank(value);
        Validate.notNull(category);

        this.value = value;
        this.category = category;
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


    /**
     * Available categories
     */
    public static enum Category {
        ANIMALS, FRUITS, VEGETABLES;

        public static Optional<Category> getByNameIgnoreCase(final String value) {
            Validate.notNull(value);

            return Stream.of(Category.values())
                    .filter(category -> category.name().equalsIgnoreCase(value))
                    .findAny();
        }
    }


    @Override
    public String toString() {
        return String.format("Secret [value=%s, category=%s]", value, category);
    }
}
