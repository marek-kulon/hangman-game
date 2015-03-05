package hangman.core;

import hangman.core.guess.Guess;
import hangman.core.secret.Secret;
import hangman.core.secret.Secret.Category;
import hangman.core.state.GameState;
import hangman.core.state.GuessAlreadyMadeException;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.*;

public class GameTest {

    private static final Secret SECRET_ANIMAL = Secret.of("dog", Category.ANIMALS);
    private static final Secret SECRET_FRUIT = Secret.of("apple", Category.FRUITS);

    private Game game;

    @Before
    public void setUp() {
        game = Game.newGame(1, SECRET_ANIMAL);
    }

    @Test
    public void makeAGuessLower() throws GuessAlreadyMadeException {
        assertTrue("correct lower", game.makeAGuess(Guess.of('d')));
    }

    @Test
    public void makeAGuessUpper() throws GuessAlreadyMadeException {
        assertTrue("correct upper", game.makeAGuess(Guess.of('O')));
    }

    @Test
    public void makeAGuessWrong() throws GuessAlreadyMadeException {
        assertFalse("incorrect", game.makeAGuess(Guess.of('z')));
    }

    @Test
    public void makeAGuessAlreadyWon() throws GuessAlreadyMadeException {
        game.makeAGuess(Guess.of('d'));
        game.makeAGuess(Guess.of('o'));
        game.makeAGuess(Guess.of('g'));
        assertFalse("won -> false", game.makeAGuess(Guess.of('d')));
    }

    @Test
    public void makeAGuessAlreadyLost() throws GuessAlreadyMadeException {
        game.makeAGuess(Guess.of('X'));
        game.makeAGuess(Guess.of('Y'));
        assertFalse("lost -> false", game.makeAGuess(Guess.of('d')));
    }

    @Test
    public void newGame() throws GuessAlreadyMadeException {
        assertEquals(0, Game.newGame(1, Secret.of("dog", Category.ANIMALS)).getGameState().getGuesses().size());
    }

    @Test
    public void restoreGame() throws GuessAlreadyMadeException {
        Game restoredGame = Game.of(game.getGameState());

        assertEquals(game, restoredGame);
    }

    @Test
    public void equalsAndHashCode() {
        Game gOne = Game.newGame(10, SECRET_ANIMAL);
        Game gTwo = Game.of(GameState.newGameState(10, SECRET_ANIMAL, Collections.emptySet()));
        Game gThree = Game.newGame(10, SECRET_ANIMAL);

        // equals
        assertFalse("equals: null", gOne.equals(null));
        assertTrue("equals: is reflexive", gOne.equals(gOne));
        assertTrue("equals: is symmetric", gOne.equals(gTwo) && gTwo.equals(gOne));
        assertTrue("equals: is transitive", gOne.equals(gTwo) && gTwo.equals(gThree) && gOne.equals(gThree));
        assertTrue("equals: is consistent", gOne.equals(gTwo) && gOne.equals(gTwo));

        Game gFruit = Game.newGame(10, SECRET_FRUIT);
        Game gEleven = Game.newGame(11, SECRET_ANIMAL);

        assertFalse(gOne.equals(gFruit));
        assertFalse(gOne.equals(gEleven));

        // hashCode
        assertEquals(gOne.hashCode(), gTwo.hashCode());
    }

}
