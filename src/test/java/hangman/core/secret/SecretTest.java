package hangman.core.secret;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import hangman.core.secret.Secret.Category;

import org.junit.Test;

public class SecretTest {

	/*
	 * getGuessesToKnowMe
	 */
	@Test
	public void getGuessesToKnowMeNo() {
		assertEquals(3, Secret.of("dog", Category.ANIMALS).getGuessesToKnowMeNo());
		assertEquals(2, Secret.of("doo", Category.ANIMALS).getGuessesToKnowMeNo());
		assertEquals(2, Secret.of("doO", Category.ANIMALS).getGuessesToKnowMeNo());
	}
	
	
	/*
	 * equals & hashCode
	 */
	
	@Test
	public void equalsAndHashCode() {
		final Secret sdOne = Secret.of("dog", Category.ANIMALS);
		final Secret scTwo = Secret.of("dog", Category.ANIMALS);
		final Secret sdThree = Secret.of("dog", Category.ANIMALS);
		
		// equals
		assertFalse("equals: null", sdOne.equals(null));
		assertTrue("equals: is reflexive", sdOne.equals(sdOne));
		assertTrue("equals: is symmetric", sdOne.equals(scTwo) && scTwo.equals(sdOne));
		assertTrue("equals: is transitive", sdOne.equals(scTwo) && scTwo.equals(sdThree) && sdOne.equals(sdThree));
		assertTrue("equals: is consistent", sdOne.equals(scTwo) && sdOne.equals(scTwo));
		
		final Secret scOne = Secret.of("cat", Category.ANIMALS);
		final Secret scVTwo = Secret.of("cat", Category.VEGETABLES);
		
		assertFalse("equals: not equal", sdOne.equals(scOne));
		assertFalse("equals: not equal", scOne.equals(scVTwo));
		
		// hashCode
		assertEquals(sdOne.hashCode(), sdThree.hashCode());
	}

}
