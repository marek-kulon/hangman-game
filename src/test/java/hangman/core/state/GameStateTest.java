package hangman.core.state;

import hangman.core.guess.Guess;
import hangman.core.secret.Secret;
import hangman.core.secret.Secret.Category;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

public class GameStateTest {
	
	private final Guess ga = Guess.newFor('a');
	private final Guess gb = Guess.newFor('b');
	
	private GameState gsNew;
	private GameState gsWithGuess;
	
	@Before
	public void setUp() throws GuessAlreadyMadeException {
		gsNew = GameState.newGameState(10, Secret.newSecret("dog", Category.ANIMALS), new HashSet<Guess>());
		gsWithGuess = GameState.newGameStateWithGuess(gsNew, ga);
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void isImmutableAddTest() {
		gsWithGuess.getGuesses().add(gb);
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void isImmutableRemoveTest() {
		gsWithGuess.getGuesses().remove(ga);
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void isImmutableClearTest() {
		gsWithGuess.getGuesses().clear();
	}
}
