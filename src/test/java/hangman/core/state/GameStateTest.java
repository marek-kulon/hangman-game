package hangman.core.state;

import hangman.core.guess.Guess;
import hangman.core.secret.Secret;
import hangman.core.secret.Secret.Category;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

public class GameStateTest {

    private final Guess ga = Guess.of('a');
    private final Guess gb = Guess.of('b');

    private GameState gsWithGuess;

    @Before
    public void setUp() {
        GameState gsNew = GameState.newGameState(10, Secret.of("dog", Category.ANIMALS), new HashSet<>());
        gsWithGuess = GameState.newGameStateWithGuess(gsNew, ga);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void isImmutableAdd() {
        gsWithGuess.getGuesses().add(gb);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void isImmutableRemove() {
        gsWithGuess.getGuesses().remove(ga);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void isImmutableClear() {
        gsWithGuess.getGuesses().clear();
    }
}
