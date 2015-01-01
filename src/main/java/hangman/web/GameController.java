package hangman.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;
import hangman.config.GameStateConfig;
import hangman.core.Game;
import hangman.core.guess.Guess;
import hangman.core.secret.Secret;
import hangman.core.secret.SecretGenerator;
import hangman.core.state.GameState;
import hangman.core.state.GuessAlreadyMadeException;
import hangman.core.state.repository.util.TokenGenerator;
import hangman.web.dto.GameDtoResponse;
import hangman.web.response.ResponseMessage;
import hangman.web.response.ResponseMessages;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 
 * @author Marek Kulon
 *
 */

// TODO - USE org.springframework.http.ResponseEntity
@Controller
public class GameController {
	
	private static final Logger log = LoggerFactory.getLogger(GameController.class);
	
	@RequestMapping(value="/database", method={GET, HEAD})
	protected String index() {
		return "database";
	}
	
	
	@RequestMapping(value = "/database/new-game/{category}/{maxIncorrectGuessesNo}", method = GET)
	protected @ResponseBody ResponseMessage<?> newGame(
			@PathVariable("category") String categoryName,
			@PathVariable("maxIncorrectGuessesNo") Integer maxIncorrectGuessesNo) {
		
		if (maxIncorrectGuessesNo == null || maxIncorrectGuessesNo < 0) {
			log.warn("incorrcet maxIncorrectGuessesNo value: {}", maxIncorrectGuessesNo);
			return ResponseMessages.newFailureMessage("INCORRECT-PARAMETER");
		}
		
		final Secret.Category category = Secret.Category.findByNameIgnoreCase(StringUtils.defaultString(categoryName));
		if (category == null) {
			log.warn("category not found: {}", categoryName);
			return ResponseMessages.newFailureMessage("CATEGORY-NOT-FOUND");
		}

		final Secret newSecret = SecretGenerator.generate(category);
		log.debug("generated secret: {}", newSecret);

		final String token = TokenGenerator.generate();
		log.debug("generated token: {}", token);

		final Game newGame = Game.newGame(maxIncorrectGuessesNo, newSecret);

		GameStateConfig.getGameStateRepository().saveOrUpdate(token, newGame.getGameState());

		return ResponseMessages.newSuccessMessage(new GameDtoResponse(token, newGame));
	}
	
	
	@RequestMapping(value="/database/load/{token}", method=GET)
	protected @ResponseBody ResponseMessage<?> load(@PathVariable("token") String token) {
		log.debug("received: {}", token);
		
		final GameState gs = GameStateConfig.getGameStateRepository().find(token);
		if(gs==null) {
			log.warn("game not found: {}", token);
			return ResponseMessages.newFailureMessage("GAME-NOT-FOUND");
		}
		return ResponseMessages.newSuccessMessage(new GameDtoResponse(token, Game.restore(gs)));
	}
	
	
	@RequestMapping(value="/database/guess/{token}/{value}", method=POST) //FIXME - MAKE POST
	protected @ResponseBody ResponseMessage<?> guess(@PathVariable("token") String token, @PathVariable("value") String value) {
		log.debug("received, token: {}, value: {}", token, value);
		
		// create guess value
		final Guess guess = value!=null && value.length()==1 && Guess.isValidGuessCharacter(value.charAt(0)) ?
				Guess.newFor(value.charAt(0)) : 
					null;
				
		if (guess==null) {
			log.warn("incorrect guess value: {}", value);
			return ResponseMessages.newFailureMessage("INCORRECT-GUESS-VALUE");
		}
		
		// get old game state from db
		final GameState gs = GameStateConfig.getGameStateRepository().find(token);
		
		if(gs==null) {
			log.warn("game not found: {}", token);
			return ResponseMessages.newFailureMessage("GAME-NOT-FOUND");
		}
		
		final Game game = Game.restore(gs);
		try {
			boolean isCorrect = game.doGuess(guess);
			log.debug("isCorrect: {}", isCorrect);
		} catch (GuessAlreadyMadeException e) {
			return ResponseMessages.newFailureMessage("ALREADY-GUESSED");
		}
		
		// save result back to db
		GameStateConfig.getGameStateRepository().saveOrUpdate(token, game.getGameState());
		
		return ResponseMessages.newSuccessMessage(new GameDtoResponse(token, game));
	}
	
	
	@ExceptionHandler(MissingServletRequestParameterException.class)
	protected @ResponseBody ResponseMessage<?> handleMissingParameters(MissingServletRequestParameterException exc) {
		return ResponseMessages.newFailureMessage("MISSING-PARAMETER ["+exc.getParameterName()+"]");
	}
}