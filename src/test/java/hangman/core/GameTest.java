package hangman.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import hangman.core.guess.Guess;
import hangman.core.secret.Secret;
import hangman.core.secret.Secret.Category;
import hangman.core.state.GuessAlreadyMadeException;

import org.junit.Before;
import org.junit.Test;

public class GameTest {
	
	private Game game;
	
	@Before
	public void setUp() {
		game = Game.newGame(2, Secret.of("dog", Category.ANIMALS));
	}

	@Test
	public void makeAGuessLower() throws GuessAlreadyMadeException {
		assertTrue("lower", game.makeAGuess(Guess.of('d')));
	}
	
	@Test
	public void makeAGuessUpper() throws GuessAlreadyMadeException {
		assertTrue(game.makeAGuess(Guess.of('O')));
	}
	
	@Test
	public void makeAGuessWrong() throws GuessAlreadyMadeException {
		assertFalse("wrong", game.makeAGuess(Guess.of('z')));
	}
	
	@Test
	public void makeAGuessAlreadyWon() throws GuessAlreadyMadeException {
		game.makeAGuess(Guess.of('d'));
		game.makeAGuess(Guess.of('o'));
		game.makeAGuess(Guess.of('g'));
		assertFalse("won -> false", game.makeAGuess(Guess.of('d')));
	}
	
	@Test
	public void makeAGuessAlreadyLost() throws GuessAlreadyMadeException {
		game.makeAGuess(Guess.of('X'));
		game.makeAGuess(Guess.of('Y'));
		assertFalse("lost -> false", game.makeAGuess(Guess.of('d')));
	}
	
	@Test
	public void newGame() throws GuessAlreadyMadeException {
		assertEquals(0, Game.newGame(2, Secret.of("dog", Category.ANIMALS)).getGameState().getGuesses().size());
	}
	
	@Test
	public void restoreGame() throws GuessAlreadyMadeException {
		Game g = Game.of(game.getGameState());
		
		assertEquals(g.getGameState(), game.getGameState());
	}

}
