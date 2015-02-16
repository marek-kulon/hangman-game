package hangman.web;

import hangman.core.Game;
import hangman.core.guess.Guess;
import hangman.core.secret.Secret;
import hangman.core.secret.service.SecretService;
import hangman.core.state.GameState;
import hangman.core.state.GuessAlreadyMadeException;
import hangman.core.state.repository.GameStateRepository;
import hangman.core.state.repository.util.TokenGenerator;
import hangman.util.AccessMonitor;
import hangman.web.exception.GameNotFoundException;
import hangman.web.exception.IllegalGuessValueException;
import hangman.web.exception.IllegalMaxIncorrectGuessesNumberException;
import hangman.web.exception.SecretCategoryNotSupportedException;
import hangman.web.transfer.GameDTO;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * 
 * @author Marek Kulon
 *
 */

@RestController
@RequestMapping("/game")
public class GameController {
	
	private static final Logger log = LoggerFactory.getLogger(GameController.class);

    // TODO add docs & externalize configuration
    private static final AccessMonitor<String, Game> mainMonitor = new AccessMonitor<>(10, 2, TimeUnit.SECONDS);
	
	@Autowired
	private SecretService secretService;
	@Autowired
	private GameStateRepository gameStateRepository;
	
	
	/**
	 * Create new game
	 * 
	 * @param categoryName name secret category
	 * @param maxIncorrectGuessesNo how many times user can make a wrong guess
	 * @return Generated game and its id
	 * @throws SecretCategoryNotSupportedException category not supported by system
	 * @throws IllegalMaxIncorrectGuessesNumberException
	 */
	@RequestMapping(value = "/new-game/{category}/{maxIncorrectGuessesNo}", method = POST, produces = "application/json")
	@ResponseStatus(HttpStatus.CREATED)
	protected Resource<GameDTO> newGame(
			@PathVariable("category") String categoryName,
			@PathVariable("maxIncorrectGuessesNo") Integer maxIncorrectGuessesNo) {

        if (maxIncorrectGuessesNo < 0) {
            log.warn("incorrect maxIncorrectGuessesNo value: {}", maxIncorrectGuessesNo);
            throw new IllegalMaxIncorrectGuessesNumberException(maxIncorrectGuessesNo);
        }

        final Secret.Category category = Secret.Category
                .getByNameIgnoreCase(categoryName)
                .orElseThrow(() -> {
                    log.warn("category not found: {}", categoryName);
                    return new SecretCategoryNotSupportedException(categoryName);
                });


		final Secret newSecret = secretService.getRandomByCategory(category);
		log.debug("generated secret: {}", newSecret);

		final String token = TokenGenerator.generate();
		log.debug("generated token: {}", token);

		final Game newGame = Game.newGame(maxIncorrectGuessesNo, newSecret);

		gameStateRepository.saveOrUpdate(token, newGame.getGameState()); // no need for mainMonitor here: new token -> no chance of race condition
		
		GameDTO gameData = new GameDTO(newGame);
		Link gameLink = new Link(token);
		return new Resource<>(gameData, gameLink);
	}
	
	
	/**
	 * Retrieve game by id
	 * 
	 * @param token game id
	 * @return saved game
	 * @throws GameNotFoundException
     * @throws hangman.util.AccessMonitor.MonitorException
	 */
	@RequestMapping(value="/load/{token}", method=GET, produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
	protected GameDTO load(@PathVariable("token") String token) {
		log.debug("received: {}", token);

        Game game = mainMonitor.tryExecute(token, () -> Game.of(loadGameState(token)));
		return new GameDTO(game);
	}
	
	
	/**
	 * Make a guess
	 * 
	 * @param token game id
	 * @param value guess made by user
	 * @return game state after operation
	 * @throws GameNotFoundException
	 * @throws IllegalGuessValueException value is not recognised as a valid character
	 * @throws GuessAlreadyMadeException if guess already had been made
     * @throws hangman.util.AccessMonitor.MonitorException
	 */
	@RequestMapping(value="/guess/{token}/{value}", method=PATCH, produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
	protected GameDTO makeAGuess(@PathVariable("token") String token, @PathVariable("value") Character value) {
		log.debug("received, token: {}, value: {}", token, value);
		
		if (!Guess.isValidGuessCharacter(value)) {
			log.warn("invalid guess value: {}", value);
			throw new IllegalGuessValueException(value);
		}
		
		final Guess guess = Guess.of(value);

        final Game gameAfterMadeGuess = mainMonitor.tryExecute(token, () -> {
            // get old game state from repository
            final Game game = Game.of(loadGameState(token));
            // perform guess operation
            boolean isCorrect = game.makeAGuess(guess);
            log.debug("isCorrect: {}", isCorrect);
            // save result back to repository
            gameStateRepository.saveOrUpdate(token, game.getGameState());
            return game;
        });

        System.out.println(gameAfterMadeGuess);

		return new GameDTO(gameAfterMadeGuess);
	}
	
	
	private GameState loadGameState(String gameId){
		Validate.notNull(gameId);

        GameState gameState = gameStateRepository
                .find(gameId)
                .orElseThrow(()  -> {
                    log.warn("game not found: {}", gameId);
                    return new GameNotFoundException(gameId);
                });

        return gameState;
	}
}