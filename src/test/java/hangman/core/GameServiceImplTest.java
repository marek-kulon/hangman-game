package hangman.core;

import com.google.common.collect.Sets;
import hangman.core.guess.Guess;
import hangman.core.secret.Secret;
import hangman.core.secret.repository.SecretRepository;
import hangman.core.state.GameState;
import hangman.core.state.repository.GameStateRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static hangman.core.GameService.IdGamePair;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class GameServiceImplTest {

    private static final Secret SECRET = Secret.of("Monkey", Secret.Category.ANIMALS);
    private static final Game GAME_NEW = Game.of(GameState.newGameState(10, SECRET, Collections.emptySet()));
    private static final GameState GAME_STATE_WITH_GUESS = GameState.newGameState(10, SECRET, Sets.newHashSet(Guess.of('x')));
    private static final Game GAME_WITH_GUESS = Game.of(GAME_STATE_WITH_GUESS);

    private static final String ID_OK = "OK";
    private static final String ID_NOT_OK = "XXX";

    @Mock
    private SecretRepository secretRepository;
    @Mock
    private GameStateRepository gameStateRepository;

    @InjectMocks
    GameServiceImpl service;

    @Before
    public void setUp() {
        // set up secret repository
        when(secretRepository.findAllByCategory(any())).thenReturn(
                Arrays.asList(SECRET)
        );
        // set up game repository
        when(gameStateRepository.find(ID_OK)).thenReturn(
                Optional.of(GAME_STATE_WITH_GUESS)
        );
        when(gameStateRepository.find(ID_NOT_OK)).thenReturn(
                Optional.empty()
        );
    }

    @Test
    public void createGame() {
        IdGamePair idGamePair = service.createGame(Secret.Category.ANIMALS, 10);

        assertNotNull(idGamePair.getGameId());
        assertEquals(GAME_NEW, idGamePair.getGame());
        verify(secretRepository).findAllByCategory(Secret.Category.ANIMALS);
    }

    @Test
    public void findGameOk() {
        Optional<Game> game = service.findGame(ID_OK);
        assertEquals(GAME_WITH_GUESS, game.get());
        verify(gameStateRepository).find(eq(ID_OK));
    }

    @Test
    public void findGameNotFound() {
        Optional<Game> game = service.findGame(ID_NOT_OK);
        assertFalse(game.isPresent());
        verify(gameStateRepository).find(eq(ID_NOT_OK));
    }

    @Test
    public void makeAGuessOk() {
        Optional<Game> game = service.makeAGuess(ID_OK, Guess.of('a'));
        assertTrue(game.get().getGameState().getGuesses().contains(Guess.of('a')));
        verify(gameStateRepository).find(eq(ID_OK));
    }

    @Test
    public void makeAGuessGameNotFound() {
        Optional<Game> game = service.makeAGuess(ID_NOT_OK, Guess.of('a'));
        assertFalse(game.isPresent());
        verify(gameStateRepository).find(eq(ID_NOT_OK));
    }
}