package hangman.core;

import com.google.common.collect.Sets;
import hangman.core.guess.Guess;
import hangman.core.secret.Secret;
import hangman.core.state.GameState;
import hangman.util.AccessMonitor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Supplier;

import static hangman.core.GameService.IdGamePair;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class GameServiceThreadSafeImplTest {

    private static final Secret SECRET = Secret.of("Monkey", Secret.Category.ANIMALS);
    private static final Game GAME_NEW = Game.of(GameState.newGameState(10, SECRET, Collections.emptySet()));
    private static final GameState GAME_STATE_WITH_GUESS = GameState.newGameState(10, SECRET, Sets.newHashSet(Guess.of('x')));
    private static final Game GAME_WITH_GUESS = Game.of(GAME_STATE_WITH_GUESS);

    private static final String ID_OK = "OK";
    private static final String ID_NOT_OK = "XXX";

    @Mock
    private GameService gameService;
    @Mock
    private AccessMonitor<String, Optional<Game>> mainMonitor;

    @InjectMocks
    private GameServiceThreadSafeImpl gameServiceThreadSafe;

    @Before
    public void setUpAccessMonitor() throws Exception {
        // invoke original supplier method
        when(mainMonitor.tryExecute(any(), any())).thenAnswer(
                invocation -> {
                    Supplier originalSupplier = invocation.getArgumentAt(1, Supplier.class);
                    return originalSupplier.get();
                }
        );
    }

    @Test
    public void createGame() throws Exception {
        when(gameService.createGame(any(), anyInt())).thenReturn(
                new IdGamePair(ID_OK, GAME_NEW)
        );

        IdGamePair idGamePair = gameServiceThreadSafe.createGame(Secret.Category.ANIMALS, 10);

        assertEquals(ID_OK, idGamePair.getGameId());
        assertEquals(GAME_NEW, idGamePair.getGame());

        verify(gameService).createGame(eq(Secret.Category.ANIMALS), eq(10));
        verifyZeroInteractions(mainMonitor);
    }

    @Test
    public void findGameOk() throws Exception {
        when(gameService.findGame(eq(ID_OK))).thenReturn(
                Optional.of(GAME_WITH_GUESS)
        );

        Optional<Game> game = gameServiceThreadSafe.findGame(ID_OK);

        assertEquals(GAME_WITH_GUESS, game.get());
        verify(gameService).findGame(eq(ID_OK));
        verify(mainMonitor).tryExecute(eq(ID_OK), any());
    }

    @Test
    public void findGameNotFound() throws Exception {
        when(gameService.findGame(eq(ID_NOT_OK))).thenReturn(
                Optional.empty()
        );

        Optional<Game> game = gameServiceThreadSafe.findGame(ID_NOT_OK);

        assertFalse(game.isPresent());
        verify(gameService).findGame(eq(ID_NOT_OK));
        verify(mainMonitor).tryExecute(eq(ID_NOT_OK), any());

    }

    @Test
    public void makeAGuessOk() throws Exception {
        when(gameService.makeAGuess(eq(ID_OK), any())).thenReturn(
                Optional.of(GAME_WITH_GUESS)
        );

        Optional<Game> game = gameServiceThreadSafe.makeAGuess(ID_OK, Guess.of('c'));

        assertEquals(GAME_WITH_GUESS, game.get());
        verify(gameService).makeAGuess(eq(ID_OK), any());
        verify(mainMonitor).tryExecute(eq(ID_OK), any());
    }

    @Test
    public void makeAGuessGameNotFound() throws Exception {
        when(gameService.makeAGuess(eq(ID_NOT_OK), any())).thenReturn(
                Optional.<Game>empty()
        );

        Optional<Game> game = gameServiceThreadSafe.makeAGuess(ID_NOT_OK, Guess.of('c'));

        assertFalse(game.isPresent());
        verify(gameService).makeAGuess(eq(ID_NOT_OK), any());
        verify(mainMonitor).tryExecute(eq(ID_NOT_OK), any());
    }
}