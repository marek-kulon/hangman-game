package hangman.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import hangman.core.Game;
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
		game = Game.newGame(2, Secret.newSecret("dog", Category.ANIMALS));
	}

	@Test
	public void doGuessLowerTest() throws GuessAlreadyMadeException {
		assertTrue("lower", game.doGuess(Guess.newGuess('d')));
	}
	
	@Test
	public void doGuessUpperTest() throws GuessAlreadyMadeException {
		assertTrue(game.doGuess(Guess.newGuess('O')));
	}
	
	@Test
	public void doGuessWrongTest() throws GuessAlreadyMadeException {
		assertFalse("wrong", game.doGuess(Guess.newGuess('z')));
	}
	
	@Test
	public void doGuessAlreadyWonTest() throws GuessAlreadyMadeException {
		game.doGuess(Guess.newGuess('d'));
		game.doGuess(Guess.newGuess('o'));
		game.doGuess(Guess.newGuess('g'));
		assertFalse("won -> false", game.doGuess(Guess.newGuess('d')));
	}
	
	@Test
	public void doGuessAlreadyLostTest() throws GuessAlreadyMadeException {
		game.doGuess(Guess.newGuess('X'));
		game.doGuess(Guess.newGuess('Y'));
		assertFalse("lost -> false", game.doGuess(Guess.newGuess('d')));
	}
	
	@Test
	public void newGameTest() throws GuessAlreadyMadeException {
		assertEquals(0, Game.newGame(2, Secret.newSecret("dog", Category.ANIMALS)).getGameState().getGuesses().size());
	}
	
	@Test
	public void restoreTest() throws GuessAlreadyMadeException {
		Game g = Game.restore(game.getGameState());
		
		assertEquals(g.getGameState(), game.getGameState());
	}

}
