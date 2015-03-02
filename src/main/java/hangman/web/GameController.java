package hangman.web;

import hangman.core.Game;
import hangman.core.GameService;
import hangman.core.GameService.IdGamePair;
import hangman.core.guess.Guess;
import hangman.core.secret.Secret;
import hangman.core.state.GuessAlreadyMadeException;
import hangman.web.exception.GameNotFoundException;
import hangman.web.exception.IllegalGuessValueException;
import hangman.web.exception.IllegalAllowedIncorrectGuessesNumberException;
import hangman.web.exception.SecretCategoryNotSupportedException;
import hangman.web.transfer.GameDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * @author Marek Kulon
 */

@RestController
@RequestMapping("/game")
public class GameController {

    private static final Logger log = LoggerFactory.getLogger(GameController.class);

    @javax.annotation.Resource(name = "gameServiceThreadSafe")
    private GameService gameService;

    /**
     * Create new game
     *
     * @param categoryName          name of secret category
     * @param allowedIncorrectGuessesNo how many times user can make a incorrect guess before loosing game
     * @return created game and its id
     * @throws SecretCategoryNotSupportedException       category not supported by system
     * @throws hangman.web.exception.IllegalAllowedIncorrectGuessesNumberException
     */
    @RequestMapping(value = "/new-game/{category}/{allowedIncorrectGuessesNo}", method = POST, produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    protected Resource<GameDTO> newGame(
            @PathVariable("category") String categoryName,
            @PathVariable("allowedIncorrectGuessesNo") Integer allowedIncorrectGuessesNo) {

        if (allowedIncorrectGuessesNo < 0) {
            log.warn("incorrect allowedIncorrectGuessesNo value: {}", allowedIncorrectGuessesNo);
            throw new IllegalAllowedIncorrectGuessesNumberException(allowedIncorrectGuessesNo);

        }

        final Secret.Category category = Secret.Category
                .getByNameIgnoreCase(categoryName)
                .orElseThrow(() -> {
                    log.warn("category not found: {}", categoryName);
                    return new SecretCategoryNotSupportedException(categoryName);
                });

        final IdGamePair idGamePair = gameService.createGame(category, allowedIncorrectGuessesNo);

        GameDTO gameData = new GameDTO(idGamePair.getGame());
        Link gameLink = new Link(idGamePair.getGameId());
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
    @RequestMapping(value = "/load/{token}", method = GET, produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    protected GameDTO load(@PathVariable("token") String token) {
        log.debug("received: {}", token);

        Game loadedGame = gameService
                .findGame(token)
                .orElseThrow(() -> {
                    log.warn("game not found: {}", token);
                    return new GameNotFoundException(token);
                });

        return new GameDTO(loadedGame);
    }


    /**
     * Make a guess
     *
     * @param token game id
     * @param value guess made by user
     * @return game state after operation
     * @throws GameNotFoundException
     * @throws IllegalGuessValueException                  value is not recognised as a valid character
     * @throws GuessAlreadyMadeException                   if guess already had been made
     * @throws hangman.util.AccessMonitor.MonitorException
     */
    @RequestMapping(value = "/guess/{token}/{value}", method = PATCH, produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    protected GameDTO makeAGuess(@PathVariable("token") String token, @PathVariable("value") Character value) {
        log.debug("received, token: {}, value: {}", token, value);

        if (!Guess.isValidGuessCharacter(value)) {
            log.warn("invalid guess value: {}", value);
            throw new IllegalGuessValueException(value);
        }

        Game gameAfterMadeGuess = gameService
                .makeAGuess(token, Guess.of(value))
                .orElseThrow(() -> {
                    log.warn("game not found: {}", token);
                    return new GameNotFoundException(token);
                });

        return new GameDTO(gameAfterMadeGuess);
    }
}