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
 */
public final class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Number of allowed incorrect guesses */
    private final int allowedIncorrectGuessesNo;

    /** Secret word to guess */
    private final Secret secret;

    /**
     * All guesses user has made so far.
     * Set is unmodifiable
     */
    private final Set<Guess> guesses;


    // do not expose - use factory methods instead
    private GameState(int allowedIncorrectGuessesNo, Secret secret, Set<Guess> guesses) {
        this.allowedIncorrectGuessesNo = allowedIncorrectGuessesNo;
        this.secret = secret;
        this.guesses = Collections.unmodifiableSet(guesses); // make sure is immutable

        // post construct: validate incorrect guesses number in relation to maximum allowed value
        if (getIncorrectGuessesNo() > getAllowedIncorrectGuessesNo()+1) {
            throw new IllegalGameStateException("Number of incorrect guesses is grater than allowed value");
        }
    }

    /**
     * Creates a new object of {@code GameState} from the specified parameters.
     *
     * @param allowedIncorrectGuessesNo maximum allowed number of incorrect guesses user can make before losing game
     * @param secret value to guess
     * @param guesses currently made guesses
     * @return created object
     */
    public static GameState newGameState(int allowedIncorrectGuessesNo, Secret secret, Set<Guess> guesses) {
        Validate.isTrue(allowedIncorrectGuessesNo >= 0);
        Validate.notNull(secret);
        Validate.noNullElements(guesses);

        return new GameState(allowedIncorrectGuessesNo,
                secret,
                guesses);
    }

    /**
     * Creates a new object of {@code GameState} from the specified game state and guess value.
     *
     * @param oldGameState the candidate game state
     * @param value guess value
     * @return created object
     * @throws GuessAlreadyMadeException if guess was already made by user
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
                oldGameState.getAllowedIncorrectGuessesNo(),
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
     * Calculate status of the game based on its current state
     *
     * @return calculated status
     */
    public GameStatus getGameStatus() {
        // users incorrect guesses number > maximum allowed value
        if (getIncorrectGuessesNo() > getAllowedIncorrectGuessesNo()) {
            return LOST;
        }
        // number of correct guesses made by user == number of guesses it takes to know the secret
        if (getCorrectGuessesNo() == getSecret().getGuessesToKnowMeNo()) {
            return WON;
        }
        // otherwise
        return IN_PROGRESS;
    }

    public int getAllowedIncorrectGuessesNo() {
        return allowedIncorrectGuessesNo;
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
        return Objects.hash(allowedIncorrectGuessesNo, secret, guesses);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (obj.getClass() != getClass()) return false;

        GameState other = (GameState) obj;
        return Objects.equals(this.allowedIncorrectGuessesNo, other.allowedIncorrectGuessesNo)
                && Objects.equals(this.secret, other.secret)
                && Objects.equals(this.guesses, other.guesses);
    }

    @Override
    public String toString() {
        return String.format("GameState [allowedIncorrectGuessesNo=%s, secret=%s, guesses=%s]", allowedIncorrectGuessesNo, secret, guesses);
    }
}
