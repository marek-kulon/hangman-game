package hangman.core.guess;

import java.io.Serializable;

import hangman.core.secret.Secret;

import org.apache.commons.lang3.Validate;

public final class Guess implements Comparable<Guess>, Serializable {
	private static final long serialVersionUID = 1L;

	private final char value;

	public boolean isCorrectFor(Secret secret) {
		Validate.notNull(secret);

		char lwrLetter = Character.toLowerCase(value);
		return secret.getValue().toLowerCase().indexOf(lwrLetter) >= 0;
	}

	public static boolean isValidGuessCharacter(char value) {
		return Secret.ALLOWED_CHARACTERS.contains(value);
	}

	public char getValue() {
		return value;
	}

	/**
	 * Creates new guess
	 * 
	 * @param value
	 * @return
	 */
	public static Guess newGuess(char value) {
		return new Guess(value);
	}

	// private constructor
	private Guess(char value) {
		Validate.isTrue(isValidGuessCharacter(value));

		this.value = value;
	}

	@Override
	public int hashCode() {
		return Character.toLowerCase(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		Guess other = (Guess) obj;
		if (Character.toLowerCase(value) != Character.toLowerCase(other.value)) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(Guess other) {
		char otherLwrLetter = other != null ? Character.toLowerCase(other
				.getValue()) : Character.MIN_VALUE;

		return Character.toLowerCase(this.getValue()) - otherLwrLetter;
	}

	@Override
	public String toString() {
		return "Guess [value=" + value + "]";
	}
}
