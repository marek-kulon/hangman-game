package hangman.core.guess;

import hangman.core.secret.Secret;
import org.apache.commons.lang3.Validate;

import java.io.Serializable;

public final class Guess implements Serializable {
    private static final long serialVersionUID = 1L;

    private final char value;

    // private constructor
    private Guess(char value) {
        Validate.isTrue(isValidGuessCharacter(value));

        this.value = value;
    }

    public static boolean isValidGuessCharacter(char value) {
        return Secret.ALLOWED_CHARACTERS.contains(value);
    }

    /**
     * Creates new guess
     *
     * @param value
     * @return
     */
    public static Guess of(char value) {
        return new Guess(value);
    }

    public boolean isCorrectFor(Secret secret) {
        Validate.notNull(secret);

        char lwrLetter = Character.toLowerCase(value);
        return secret.getValue().toLowerCase().indexOf(lwrLetter) >= 0;
    }

    public char getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Character.toLowerCase(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;
        if (obj.getClass() != getClass()) return false;

        Guess other = (Guess) obj;
        return Character.toLowerCase(value) == Character.toLowerCase(other.value);
    }

    @Override
    public String toString() {
        return String.format("Guess [value=%s]", value);
    }
}
