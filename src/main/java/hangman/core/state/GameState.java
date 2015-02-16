package hangman.core.state;

import hangman.core.guess.Guess;
import hangman.core.secret.Secret;
import org.apache.commons.lang3.Validate;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static hangman.core.Game.GameStatus;
import static hangman.core.Game.GameStatus.*;

/**
 * Immutable game state
 *
 * @author Marek Kulon
 *
 */
public final class GameState implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/** Maximum number of incorrect guesses  */
	private final int maxIncorrectGuessesNo;
	/** Secret word to guess */
	private final Secret secret;
	/** All guesses user has made so far */
	private final Set<Guess> guesses;
	
	/**
	 * 
	 * @param maxIncorrectGuessesNo
	 * @param secret
	 * @param guesses
	 * @return
	 */
	public static GameState newGameState(int maxIncorrectGuessesNo, Secret secret, Set<Guess> guesses) {
		Validate.isTrue(maxIncorrectGuessesNo >= 0);
		Validate.notNull(secret);
		Validate.noNullElements(guesses);
		
		return new GameState(maxIncorrectGuessesNo,
				secret,
				guesses);
	}

	/**
	 * 
	 * @param oldGameState
	 * @param value
	 * @return
	 * @throws GuessAlreadyMadeException
	 */
	public static GameState newGameStateWithGuess(GameState oldGameState, Guess value) {
		Validate.notNull(oldGameState);
		Validate.notNull(value);

		if (oldGameState.isGuessedAlready(value)) {
			throw new GuessAlreadyMadeException(value);
		}

		Set<Guess> newGuesses = new HashSet<>();
		newGuesses.addAll(oldGameState.getGuesses());
		newGuesses.add(value);

		return new GameState(
				oldGameState.getMaxIncorrectGuessesNo(),
				oldGameState.getSecret(),
				newGuesses);
	}
	
	/**
	 * @return number of right guesses made by user
	 */
	public long getCorrectGuessesNo() {
        return getGuesses().stream()
                .filter(g -> g.isCorrectFor(getSecret()))
                .count();
	}
	
	/**
	 * @return number of wrong guesses made by user
	 */
	public long getIncorrectGuessesNo() {
		return guesses.size() - getCorrectGuessesNo();
	}
	
	
	/**
	 * Calculate status/progress of the game based on its current state
	 * 
	 * @return
	 */
	public GameStatus getGameStatus() {
        final long incorrectGuessesNo = getIncorrectGuessesNo();

		// users incorrect guesses number >= maximum value
		if (incorrectGuessesNo > 0 && incorrectGuessesNo >= getMaxIncorrectGuessesNo()) {
			return LOST;
		}
		// number of correct guesses made by user == number of guesses it takes to know the secret
		if (getCorrectGuessesNo() == getSecret().getGuessesToKnowMeNo()) {
			return WON;
		}
		// otherwise
		return IN_PROGRESS;
	}
	
	
	/*
	 *  Private constructor
	 *  Set of guesses is unmodifiable. Better solution would be 
	 *  a set collection without 'add', 'remove', 'clear' methods in its API
	 */
	private GameState(int maxIncorrectGuessesNo, Secret secret, Set<Guess> guesses) {
		this.maxIncorrectGuessesNo = maxIncorrectGuessesNo;
		this.secret = secret;
		this.guesses = Collections.unmodifiableSet(guesses); // make sure is immutable
		
		// post construct: validate incorrect guesses number in relation to maximum allowed value
		if (getMaxIncorrectGuessesNo()!=0 && getIncorrectGuessesNo() > getMaxIncorrectGuessesNo()) {
			throw new IllegalGameStateException("Number of incorrect guesses is grater than maximum allowed value");
		}
	}

	public int getMaxIncorrectGuessesNo() {
		return maxIncorrectGuessesNo;
	}

	public Secret getSecret() {
		return secret;
	}

	public Set<Guess> getGuesses() {
		return guesses;
	}
	
	private boolean isGuessedAlready(Guess value) {
		return getGuesses().contains(value);
	}

	@Override
	public int hashCode() {
        return Objects.hash(maxIncorrectGuessesNo, secret, guesses);
	}

	@Override
	public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (obj.getClass() != getClass()) return false;

        GameState other = (GameState) obj;
        return Objects.equals(this.maxIncorrectGuessesNo, other.maxIncorrectGuessesNo)
                && Objects.equals(this.secret, other.secret)
                && Objects.equals(this.guesses, other.guesses);
	}

	@Override
	public String toString() {
        return String.format("GameState [maxIncorrectGuessesNo=%s, secret=%s, guesses=%s]", maxIncorrectGuessesNo, secret, guesses);
	}
}
