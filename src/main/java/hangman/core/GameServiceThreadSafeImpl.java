package hangman.core;

import hangman.core.guess.Guess;
import hangman.core.secret.Secret;
import hangman.util.AccessMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * Thread safe implementation of {@link GameService}.
 * Implementation uses {@link AccessMonitor#tryExecute(Object, java.util.function.Supplier)} version of monitor methods
 * allowing for fail-fast behaviour.
 */
@Service("gameServiceThreadSafe")
public class GameServiceThreadSafeImpl implements GameService {

    // safer to compose than inherit:
    // composition will protect class from unsafe method implementation inherited from upper layer
    @Resource(name = "gameService")
    private GameService gameService;

    @Autowired
    private AccessMonitor<String, Optional<Game>> mainMonitor;

    /**
     * Creates new game, generates its id and saves game in repository.
     *
     * @param category              category of a secret
     * @param allowedIncorrectGuessesNo maximum number or incorrect guesses user can make
     * @return pair of values: game id, game
     * @see GameService#createGame(hangman.core.secret.Secret.Category, int)
     */
    @Override
    public IdGamePair createGame(Secret.Category category, int allowedIncorrectGuessesNo) {
        // no need for wrapping code in access monitor here - no race conditions, no 'read/modify/write'
        return gameService.createGame(category, allowedIncorrectGuessesNo);
    }

    /**
     * Finds game in repository.
     *
     * @param gameId id value assigned to game
     * @return found game or empty value if game wasn't found
     * @see GameService#findGame(String)
     */
    @Override
    public Optional<Game> findGame(String gameId) {
        return mainMonitor.tryExecute(gameId, () -> gameService.findGame(gameId));
    }

    /**
     * Finds game in repository, performs guess operation on it and saves result in repository.
     *
     * @param gameId id value assigned to game
     * @param guess  value of guess made by user
     * @return game after performed operation or empty value if game wasn't found in repository
     * @see GameService#makeAGuess(String, hangman.core.guess.Guess)
     */
    @Override
    public Optional<Game> makeAGuess(String gameId, Guess guess) {
        return mainMonitor.tryExecute(gameId, () -> gameService.makeAGuess(gameId, guess));
    }
}
