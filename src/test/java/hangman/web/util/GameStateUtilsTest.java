package hangman.web.util;

import static org.junit.Assert.assertEquals;
import hangman.core.guess.Guess;
import hangman.core.secret.Secret;
import hangman.core.secret.Secret.Category;
import hangman.core.state.GameState;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

public class GameStateUtilsTest {

	private GameState gs ;
	
	@SuppressWarnings("serial")
	@Before
	public void before() {
		gs = GameState.newGameState(10, Secret.newSecret("Big Monkey", Category.ANIMALS), new HashSet<Guess>(){{
			add(Guess.newGuess('g'));
			add(Guess.newGuess('K'));
		}});
	}
	
	
	@Test
	public void getGuessedValueTest() {
		assertEquals("__g ___k__", GameStateUtils.getGuessedValue(gs, '_'));
	}

}
