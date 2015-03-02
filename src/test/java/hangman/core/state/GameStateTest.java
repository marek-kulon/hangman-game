package hangman.core.state;

import com.google.common.collect.Sets;
import hangman.core.guess.Guess;
import hangman.core.secret.Secret;
import hangman.core.secret.Secret.Category;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GameStateTest {

    private static final Guess GUESS_A = Guess.of('a');
    private static final Guess GUESS_B = Guess.of('b');
    private static final Secret SECRET_ANIMAL = Secret.of("dog", Category.ANIMALS);
    private static final Secret SECRET_FRUIT= Secret.of("apple", Category.FRUITS);


    private GameState gsWithGuess;

    @Before
    public void setUp() {
        GameState gsNew = GameState.newGameState(10, Secret.of("dog", Category.ANIMALS), new HashSet<>());
        gsWithGuess = GameState.newGameStateWithGuess(gsNew, GUESS_A);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void isImmutableAdd() {
        gsWithGuess.getGuesses().add(GUESS_B);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void isImmutableRemove() {
        gsWithGuess.getGuesses().remove(GUESS_A);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void isImmutableClear() {
        gsWithGuess.getGuesses().clear();
    }

    @Test
    public void equalsAndHashCode() {
        GameState gsOne = GameState.newGameState(10, SECRET_ANIMAL, Collections.emptySet());
        GameState gsTwo = GameState.newGameState(10, SECRET_ANIMAL, Collections.emptySet());
        GameState gsThree = GameState.newGameState(10, SECRET_ANIMAL, Collections.emptySet());

        // equals
        assertFalse("equals: null", gsOne.equals(null));
        assertTrue("equals: is reflexive", gsOne.equals(gsOne));
        assertTrue("equals: is symmetric", gsOne.equals(gsTwo) && gsTwo.equals(gsOne));
        assertTrue("equals: is transitive", gsOne.equals(gsTwo) && gsTwo.equals(gsThree) && gsOne.equals(gsThree));
        assertTrue("equals: is consistent", gsOne.equals(gsTwo) && gsOne.equals(gsTwo));

        GameState gsElevenAllowedIncorrect = GameState.newGameState(11, SECRET_ANIMAL, Collections.emptySet());
        GameState gsFruit = GameState.newGameState(10, SECRET_FRUIT, Collections.emptySet());
        GameState gsAGuess = GameState.newGameState(10, SECRET_ANIMAL, Sets.newHashSet(GUESS_A));

        assertFalse(gsOne.equals(gsElevenAllowedIncorrect));
        assertFalse(gsOne.equals(gsFruit));
        assertFalse(gsOne.equals(gsAGuess));

        // hashCode
        assertEquals(gsOne.hashCode(), gsTwo.hashCode());
    }
}
