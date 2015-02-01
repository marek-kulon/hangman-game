package hangman.core.guess;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import hangman.core.secret.Secret;
import hangman.core.secret.Secret.Category;

import org.junit.Test;

public class GuessTest {
	
	/*
	 * is-correct tests
	 */

	@Test(expected=NullPointerException.class)
	public void isCorrectForNull() {
		assertTrue(Guess.of('a').isCorrectFor(null));
	}
	
	@Test
	public void isCorrectForLowerUpper() {
		assertTrue(Guess.of('a').isCorrectFor(Secret.of("ALBATROSS", Category.ANIMALS)));
	}
	
	@Test
	public void isCorrectForUpperLower() {
		assertTrue(Guess.of('A').isCorrectFor(Secret.of("albatross", Category.ANIMALS)));
	}
	
	@Test
	public void isCorrectForLowerLower() {
		assertTrue(Guess.of('a').isCorrectFor(Secret.of("albatross", Category.ANIMALS)));
	}
	
	@Test
	public void isCorrectForUpperUpper() {
		assertTrue(Guess.of('A').isCorrectFor(Secret.of("ALBATROSS", Category.ANIMALS)));
	}
	
	@Test
	public void isCorrectForIcorrectGuess() {
		assertFalse(Guess.of('z').isCorrectFor(Secret.of("Albatross", Category.ANIMALS)));
	}
	
	/*
	 * id-valid guess tests
	 */
	
	@Test
	public void isValidLowerAlfaTrue() {
		assertTrue(Guess.isValidGuessCharacter('a'));
	}
	
	@Test
	public void isValidUpperAlfaTrue() {
		assertTrue(Guess.isValidGuessCharacter('A'));
	}
	
	@Test
	public void isValidSpaceFalse() {
		assertFalse(Guess.isValidGuessCharacter(' '));
	}
	
	@Test
	public void isValidDigitFalse() {
		assertFalse(Guess.isValidGuessCharacter('1'));
	}
	
	@Test
	public void isValidDotFalse() {
		assertFalse(Guess.isValidGuessCharacter('.'));
	}
	
	/*
	 * equals & hashCode
	 */
	
	@Test
	public void equalsAndHashCode() {
		final Guess gaOne = Guess.of('a');
		final Guess gaTwo = Guess.of('a');
		final Guess gaThree = Guess.of('a');
		final Guess gAOne = Guess.of('A');
		
		// equals
		assertFalse("equals: null", gaOne.equals(null));
		assertTrue("equals: is reflexive", gaOne.equals(gaOne));
		assertTrue("equals: is symmetric", gaOne.equals(gaTwo) && gaTwo.equals(gaOne));
		assertTrue("equals: is transitive", gaOne.equals(gaTwo) && gaTwo.equals(gaThree) && gaOne.equals(gaThree));
		assertTrue("equals: is consistent", gaOne.equals(gaTwo) && gaOne.equals(gaTwo));
		
		final Guess gzOne = Guess.of('z');
		
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
		assertTrue(Guess.of('b').compareTo(Guess.of('a')) > 0);
		assertTrue(Guess.of('b').compareTo(Guess.of('A')) > 0);
		assertTrue(Guess.of('B').compareTo(Guess.of('a')) > 0);
		assertTrue(Guess.of('B').compareTo(Guess.of('A')) > 0);
		
		assertEquals(0, Guess.of('a').compareTo(Guess.of('a')));
		assertEquals(0, Guess.of('a').compareTo(Guess.of('A')));
		assertEquals(0, Guess.of('A').compareTo(Guess.of('A')));
		assertEquals(0, Guess.of('A').compareTo(Guess.of('a')));
		
		assertTrue(Guess.of('a').compareTo(Guess.of('b')) < 0);
		assertTrue(Guess.of('a').compareTo(Guess.of('B')) < 0);
		assertTrue(Guess.of('A').compareTo(Guess.of('b')) < 0);
		assertTrue(Guess.of('A').compareTo(Guess.of('B')) < 0);
		
		assertTrue(Guess.of('A').compareTo(null) > 0);
		assertTrue(Guess.of('a').compareTo(null) > 0);
	}
}
