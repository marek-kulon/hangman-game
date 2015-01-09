package hangman.core.guess;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import hangman.core.guess.Guess;
import hangman.core.secret.Secret;
import hangman.core.secret.Secret.Category;

import org.junit.Test;

public class GuessTest {
	
	/*
	 * is-correct tests
	 */

	@Test(expected=NullPointerException.class)
	public void isCorrectForNullTest() {
		assertTrue(Guess.newGuess('a').isCorrectFor(null));
	}
	
	@Test
	public void isCorrectForLowerUpperTest() {
		assertTrue(Guess.newGuess('a').isCorrectFor(Secret.newSecret("ALBATROSS", Category.ANIMALS)));
	}
	
	@Test
	public void isCorrectForUpperLowerTest() {
		assertTrue(Guess.newGuess('A').isCorrectFor(Secret.newSecret("albatross", Category.ANIMALS)));
	}
	
	@Test
	public void isCorrectForLowerLowerTest() {
		assertTrue(Guess.newGuess('a').isCorrectFor(Secret.newSecret("albatross", Category.ANIMALS)));
	}
	
	@Test
	public void isCorrectForUpperUpperTest() {
		assertTrue(Guess.newGuess('A').isCorrectFor(Secret.newSecret("ALBATROSS", Category.ANIMALS)));
	}
	
	@Test
	public void isCorrectForIcorrectGuessTest() {
		assertFalse(Guess.newGuess('z').isCorrectFor(Secret.newSecret("Albatross", Category.ANIMALS)));
	}
	
	/*
	 * id-valid guess tests
	 */
	
	@Test
	public void isValidLowerAlfaTrueTest() {
		assertTrue(Guess.isValidGuessCharacter('a'));
	}
	
	@Test
	public void isValidUpperAlfaTrueTest() {
		assertTrue(Guess.isValidGuessCharacter('A'));
	}
	
	@Test
	public void isValidSpaceFalseTest() {
		assertFalse(Guess.isValidGuessCharacter(' '));
	}
	
	@Test
	public void isValidDigitFalseTest() {
		assertFalse(Guess.isValidGuessCharacter('1'));
	}
	
	@Test
	public void isValidDotFalseTest() {
		assertFalse(Guess.isValidGuessCharacter('.'));
	}
	
	/*
	 * equals & hashCode
	 */
	
	@Test
	public void equalsAndHashCodeTest() {
		final Guess gaOne = Guess.newGuess('a');
		final Guess gaTwo = Guess.newGuess('a');
		final Guess gaThree = Guess.newGuess('a');
		final Guess gAOne = Guess.newGuess('A');
		
		// equals
		assertFalse("equals: null", gaOne.equals(null));
		assertTrue("equals: is reflexive", gaOne.equals(gaOne));
		assertTrue("equals: is symmetric", gaOne.equals(gaTwo) && gaTwo.equals(gaOne));
		assertTrue("equals: is transitive", gaOne.equals(gaTwo) && gaTwo.equals(gaThree) && gaOne.equals(gaThree));
		assertTrue("equals: is consistent", gaOne.equals(gaTwo) && gaOne.equals(gaTwo));
		
		final Guess gzOne = Guess.newGuess('z');
		
		assertFalse("equals: not equal", gaOne.equals(gzOne));
		assertTrue("equals: lower & upper cases", gaOne.equals(gAOne) && gAOne.equals(gaOne));
		
		// hashCode
		assertEquals(gaOne.hashCode(), gAOne.hashCode());
	}
	
	/*
	 * compare
	 */
	
	@Test
	public void compareTest() {
		assertTrue(Guess.newGuess('b').compareTo(Guess.newGuess('a')) > 0);
		assertTrue(Guess.newGuess('b').compareTo(Guess.newGuess('A')) > 0);
		assertTrue(Guess.newGuess('B').compareTo(Guess.newGuess('a')) > 0);
		assertTrue(Guess.newGuess('B').compareTo(Guess.newGuess('A')) > 0);
		
		assertEquals(0, Guess.newGuess('a').compareTo(Guess.newGuess('a')));
		assertEquals(0, Guess.newGuess('a').compareTo(Guess.newGuess('A')));
		assertEquals(0, Guess.newGuess('A').compareTo(Guess.newGuess('A')));
		assertEquals(0, Guess.newGuess('A').compareTo(Guess.newGuess('a')));
		
		assertTrue(Guess.newGuess('a').compareTo(Guess.newGuess('b')) < 0);
		assertTrue(Guess.newGuess('a').compareTo(Guess.newGuess('B')) < 0);
		assertTrue(Guess.newGuess('A').compareTo(Guess.newGuess('b')) < 0);
		assertTrue(Guess.newGuess('A').compareTo(Guess.newGuess('B')) < 0);
		
		assertTrue(Guess.newGuess('A').compareTo(null) > 0);
		assertTrue(Guess.newGuess('a').compareTo(null) > 0);
	}
}
