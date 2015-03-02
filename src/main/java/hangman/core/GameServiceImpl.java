package hangman.core;

import hangman.core.guess.Guess;
import hangman.core.secret.Secret;
import hangman.core.secret.repository.SecretRepository;
import hangman.core.state.GameState;
import hangman.core.state.repository.GameStateRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * Simple implementation of {@link GameService}. It makes no guarantees as to the
 * thread safety of its methods.
 */
@Service("gameService")
public class GameServiceImpl implements GameService {

    private static final Logger log = LoggerFactory.getLogger(GameServiceImpl.class);

    @Resource(name = "secretRepository")
    private SecretRepository secretRepository;

    @Resource(name = "gameStateRepository")
    private GameStateRepository gameStateRepository;


    /**
     * Creates new game, generates its id and saves game in repository.
     * Game secret is randomly generated out of values found in secrets repository.
     * Game id value is a randomly generated string.
     *
     * @param category              category of a secret
     * @param allowedIncorrectGuessesNo maximum number or incorrect guesses user can make before losing game
     * @return pair of values: game id, game
     */
    @Override
    public IdGamePair createGame(Secret.Category category, int allowedIncorrectGuessesNo) {
        notNull(category);
        isTrue(allowedIncorrectGuessesNo >= 0);

        final List<Secret> secrets = secretRepository.findAllByCategory(category);
        if (secrets == null || secrets.isEmpty()) {
            throw new IllegalStateException("no secrets in repository");
        }

        final Secret randomSecret = secrets.get(RandomUtils.nextInt(0, secrets.size()));
        log.debug("generated secret: {}", randomSecret);

        final String gameId = RandomStringUtils.randomAlphanumeric(32);
        log.debug("generated gameId: {}", gameId);

        final Game newGame = Game.newGame(allowedIncorrectGuessesNo, randomSecret);
        gameStateRepository.saveOrUpdate(gameId, newGame.getGameState());

        return new IdGamePair(gameId, newGame);
    }

    /**
     * Finds game in repository.
     *
     * @param gameId id value assigned to game
     * @return found game or empty value if game wasn't found
     */
    @Override
    public Optional<Game> findGame(String gameId) {
        notNull(gameId);

        Optional<GameState> gameState = gameStateRepository.find(gameId);
        log.debug("result: {}", gameState);

        return gameState.map(Game::of);
    }

    /**
     * Finds game in repository, performs guess operation on it and saves result in repository.
     *
     * @param gameId id value assigned to game
     * @param guess  value of guess made by user
     * @return game after performed operation or empty value if game wasn't found in repository
     */
    @Override
    public Optional<Game> makeAGuess(String gameId, Guess guess) {
        notNull(gameId);
        notNull(guess);

        final Optional<Game> game = findGame(gameId);

        game.ifPresent((gameVal) -> {
            // perform guess operation
            boolean isCorrect = gameVal.makeAGuess(guess);
            log.debug("isCorrect: {}", isCorrect);

            // save result in repository
            gameStateRepository.saveOrUpdate(gameId, gameVal.getGameState());
        });

        return game;
    }
}
