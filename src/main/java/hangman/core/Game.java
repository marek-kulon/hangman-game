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
     * @param allowedIncorrectGuessesNo maximum allowed number of incorrect guesses user can make before losing game
     * @param secret secret word to guess
     * @return created game object
     */
    public static Game newGame(int allowedIncorrectGuessesNo, Secret secret) {
        isTrue(allowedIncorrectGuessesNo >= 0);
        notNull(secret);

        GameState newGameState = GameState.newGameState(
                allowedIncorrectGuessesNo,
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
     * Creates a {@link Game} from the specified game state.
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
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (obj.getClass() != getClass()) return false;

        Game other = (Game) obj;
        return Objects.equals(this.getGameState(), other.getGameState());
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
     * <li>{@link #LOST}<br>
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
