package hangman.core;

import hangman.core.guess.Guess;
import hangman.core.secret.Secret;
import hangman.core.state.GameState;
import hangman.core.state.GuessAlreadyMadeException;

import java.util.HashSet;

import org.apache.commons.lang3.Validate;

/**
 * 
 * @author Marek Kulon
 *
 */
public class Game {

	private GameState gameState;

	/**
	 * 'Guess' action
	 * 
	 * @param guess
	 * @return true if:
	 * - game wasn't finished before
	 * - guess is right
	 * @throws GuessAlreadyMadeException
	 */
	public boolean doGuess(Guess guess) throws GuessAlreadyMadeException {
		Validate.notNull(guess);
		
		if (!GameStatus.IN_PROGRESS.equals(gameState.getGameStatus())) {
			return false;
		}
		
		this.gameState = GameState.newGameStateWithGuess(gameState, guess);
		return guess.isCorrectFor(gameState.getSecret());
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
				new HashSet<Guess>()); // no guesses
		return new Game(newGameState);
	}
	
	/**
	 * Recreate game out of given state
	 * 
	 * @param gameState
	 * @return
	 */
	public static Game restore(GameState gameState) {
		 return new Game(Validate.notNull(gameState));
	}
	
	public GameState getGameState() {
		return gameState;
	}
	
	
	// private constructor
	private Game(GameState gameState) {
		this.gameState = gameState;
	}


	@Override
	public String toString() {
		return "Game [gameState=" + gameState + "]";
	}
}
