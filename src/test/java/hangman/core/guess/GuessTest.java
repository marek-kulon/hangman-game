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
		assertTrue(Guess.newFor('a').isCorrectFor(null));
	}
	
	@Test
	public void isCorrectForLowerUpperTest() {
		assertTrue(Guess.newFor('a').isCorrectFor(Secret.newSecret("ALBATROSS", Category.ANIMALS)));
	}
	
	@Test
	public void isCorrectForUpperLowerTest() {
		assertTrue(Guess.newFor('A').isCorrectFor(Secret.newSecret("albatross", Category.ANIMALS)));
	}
	
	@Test
	public void isCorrectForLowerLowerTest() {
		assertTrue(Guess.newFor('a').isCorrectFor(Secret.newSecret("albatross", Category.ANIMALS)));
	}
	
	@Test
	public void isCorrectForUpperUpperTest() {
		assertTrue(Guess.newFor('A').isCorrectFor(Secret.newSecret("ALBATROSS", Category.ANIMALS)));
	}
	
	@Test
	public void isCorrectForIcorrectGuessTest() {
		assertFalse(Guess.newFor('z').isCorrectFor(Secret.newSecret("Albatross", Category.ANIMALS)));
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
		final Guess gaOne = Guess.newFor('a');
		final Guess gaTwo = Guess.newFor('a');
		final Guess gaThree = Guess.newFor('a');
		final Guess gAOne = Guess.newFor('A');
		
		// equals
		assertFalse("equals: null", gaOne.equals(null));
		assertTrue("equals: is reflexive", gaOne.equals(gaOne));
		assertTrue("equals: is symmetric", gaOne.equals(gaTwo) && gaTwo.equals(gaOne));
		assertTrue("equals: is transitive", gaOne.equals(gaTwo) && gaTwo.equals(gaThree) && gaOne.equals(gaThree));
		assertTrue("equals: is consistent", gaOne.equals(gaTwo) && gaOne.equals(gaTwo));
		
		final Guess gzOne = Guess.newFor('z');
		
		assertFalse("equals: not equal", gaOne.equals(gzOne));
		assertTrue("equals: lower & upper cases", gaOne.equals(gAOne) && gAOne.equals(gaOne));
		
		// hashCode
		assertEquals(gaOne, gAOne);
	}
	
	/*
	 * compare
	 */
	
	@Test
	public void compareTest() {
		assertTrue(Guess.newFor('b').compareTo(Guess.newFor('a')) > 0);
		assertTrue(Guess.newFor('b').compareTo(Guess.newFor('A')) > 0);
		assertTrue(Guess.newFor('B').compareTo(Guess.newFor('a')) > 0);
		assertTrue(Guess.newFor('B').compareTo(Guess.newFor('A')) > 0);
		
		assertEquals(0, Guess.newFor('a').compareTo(Guess.newFor('a')));
		assertEquals(0, Guess.newFor('a').compareTo(Guess.newFor('A')));
		assertEquals(0, Guess.newFor('A').compareTo(Guess.newFor('A')));
		assertEquals(0, Guess.newFor('A').compareTo(Guess.newFor('a')));
		
		assertTrue(Guess.newFor('a').compareTo(Guess.newFor('b')) < 0);
		assertTrue(Guess.newFor('a').compareTo(Guess.newFor('B')) < 0);
		assertTrue(Guess.newFor('A').compareTo(Guess.newFor('b')) < 0);
		assertTrue(Guess.newFor('A').compareTo(Guess.newFor('B')) < 0);
		
		assertTrue(Guess.newFor('A').compareTo(null) > 0);
		assertTrue(Guess.newFor('a').compareTo(null) > 0);
	}
}
