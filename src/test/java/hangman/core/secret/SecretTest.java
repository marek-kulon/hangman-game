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
	public void getGuessesToKnowMeNoTest() {
		
		assertEquals(3, Secret.newSecret("dog", Category.ANIMALS).getGuessesToKnowMeNo());
		assertEquals(2, Secret.newSecret("doo", Category.ANIMALS).getGuessesToKnowMeNo());
		assertEquals(2, Secret.newSecret("doO", Category.ANIMALS).getGuessesToKnowMeNo());
	}
	
	
	/*
	 * equals & hashCode
	 */
	
	@Test
	public void equalsAndHashCodeTest() {
		final Secret sdOne = Secret.newSecret("dog", Category.ANIMALS);
		final Secret scTwo = Secret.newSecret("dog", Category.ANIMALS);
		final Secret sdThree = Secret.newSecret("dog", Category.ANIMALS);
		
		// equals
		assertFalse("equals: null", sdOne.equals(null));
		assertTrue("equals: is reflexive", sdOne.equals(sdOne));
		assertTrue("equals: is symmetric", sdOne.equals(scTwo) && scTwo.equals(sdOne));
		assertTrue("equals: is transitive", sdOne.equals(scTwo) && scTwo.equals(sdThree) && sdOne.equals(sdThree));
		assertTrue("equals: is consistent", sdOne.equals(scTwo) && sdOne.equals(scTwo));
		
		final Secret sDOne = Secret.newSecret("Dog", Category.ANIMALS);
		final Secret scOne = Secret.newSecret("cat", Category.ANIMALS);
		final Secret scVTwo = Secret.newSecret("cat", Category.VEGETABLES);
		
		assertFalse("equals: not equal", sdOne.equals(scOne));
		assertFalse("equals: not equal", scOne.equals(scVTwo));
		assertTrue("equals: lower & upper cases", sdOne.equals(sDOne) && sDOne.equals(sdOne));
		
		// hashCode
		assertEquals(sdOne, sDOne);
	}
	
	

}