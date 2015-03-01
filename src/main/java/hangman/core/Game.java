package hangman.core;

import hangman.core.guess.Guess;
import hangman.core.secret.Secret;
import hangman.core.state.GameState;
import hangman.core.state.GuessAlreadyMadeException;
import org.apache.commons.lang3.Validate;

import java.util.Collections;
import java.util.Objects;

/**
 * @author Marek Kulon
 */
public class Game {

    private GameState gameState;

    // private constructor
    private Game(GameState gameState) {
        this.gameState = gameState;
    }

    /**
     * 'New Game' action
     *
     * @param maxIncorrectGuessesNo
     * @param secret
     * @return
     */
    public static Game newGame(int maxIncorrectGuessesNo, Secret secret) {
        Validate.isTrue(maxIncorrectGuessesNo >= 0);
        Validate.notNull(secret);

        GameState newGameState = GameState.newGameState(
                maxIncorrectGuessesNo,
                secret,
                Collections.emptySet()); // no guesses
        return new Game(newGameState);
    }

    /**
     * Recreate game out of given state
     *
     * @param gameState
     * @return
     */
    public static Game of(GameState gameState) {
        return new Game(Validate.notNull(gameState));
    }

    /**
     * 'Make a guess' action
     *
     * @param guess
     * @return true if:
     * - game wasn't finished before
     * - guess is right
     * @throws GuessAlreadyMadeException
     */
    public boolean makeAGuess(Guess guess) throws GuessAlreadyMadeException {
        Validate.notNull(guess);

        if (!GameStatus.IN_PROGRESS.equals(gameState.getGameStatus())) {
            return false;
        }

        this.gameState = GameState.newGameStateWithGuess(gameState, guess);
        return guess.isCorrectFor(gameState.getSecret());
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
        if (other == null || other.getClass() != getClass()) return false;

        return Objects.equals(this.getGameState(), ((Game) other).getGameState());
    }

    @Override
    public String toString() {
        return String.format("Game [gameState=%s]", gameState);
    }

    /**
     * Game can only by in one of listed states.
     */
    public static enum GameStatus {
        WON, LOST, IN_PROGRESS
    }
}
