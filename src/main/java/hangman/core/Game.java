package hangman.core;

import hangman.core.guess.Guess;
import hangman.core.secret.Secret;
import hangman.core.state.GameState;
import hangman.core.state.GuessAlreadyMadeException;

import java.util.Collections;
import java.util.Objects;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * @author Marek Kulon
 */
public class Game {

    private GameState gameState;

    // do not expose - factory methods should be used instead
    private Game(GameState gameState) {
        this.gameState = gameState;
    }

    /**
     * New game action
     *
     * @param maxIncorrectGuessesNo maximum number of incorrect guesses user can make
     * @param secret secret word to guess
     * @return created game object
     */
    public static Game newGame(int maxIncorrectGuessesNo, Secret secret) {
        isTrue(maxIncorrectGuessesNo >= 0);
        notNull(secret);

        GameState newGameState = GameState.newGameState(
                maxIncorrectGuessesNo,
                secret,
                Collections.emptySet()); // no guesses at the beginning
        return new Game(newGameState);
    }

    /**
     * Make a guess action
     *
     * @param guess the candidate guess value
     * @return {@code true} if game wasn't finished before and the guess is correct
     * @throws GuessAlreadyMadeException if the same guess was already made
     */
    public boolean makeAGuess(Guess guess) throws GuessAlreadyMadeException {
        notNull(guess);

        if (!GameStatus.IN_PROGRESS.equals(gameState.getGameStatus())) {
            return false;
        }

        this.gameState = GameState.newGameStateWithGuess(gameState, guess);
        return guess.isCorrectFor(gameState.getSecret());
    }

    /**
     * Creates a {@code Game} from the specified game state.
     *
     * @param gameState the candidate game state
     * @return created game object
     */
    public static Game of(GameState gameState) {
        return new Game(notNull(gameState));
    }

    public GameState getGameState() {
        return gameState;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getGameState());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null) return false;
        if (other.getClass() != getClass()) return false;

        return Objects.equals(this.getGameState(), ((Game) other).getGameState());
    }

    @Override
    public String toString() {
        return String.format("Game [gameState=%s]", gameState);
    }

    /**
     * A game can be in one of the following states:
     * <ul>
     * <li>{@link #IN_PROGRESS}<br>
     *     Game is in progress
     *     </li>
     * <li>{@link #WON}<br>
     *     User won the game
     *     </li>
     * <li>{@link #WON}<br>
     *     User lost the game
     *     </li>
     * </ul>
     */
    public static enum GameStatus {
        /**
         * Game status for a game that is still in progress
         */
        IN_PROGRESS,

        /**
         * Game status for a game won by user
         */
        WON,

        /**
         * Game status for a game lost by user
         */
        LOST
    }
}
