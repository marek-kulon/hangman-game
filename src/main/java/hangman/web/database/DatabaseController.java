package hangman.web.database;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;
import hangman.config.GameStateConfig;
import hangman.core.Game;
import hangman.core.guess.Guess;
import hangman.core.secret.Secret;
import hangman.core.secret.SecretGenerator;
import hangman.core.state.GameState;
import hangman.core.state.GuessAlreadyMadeException;
import hangman.core.state.repository.util.TokenGenerator;
import hangman.web.common.response.ResponseMessage;
import hangman.web.common.response.ResponseMessages;
import hangman.web.database.dto.GameDtoResponse;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 
 * @author Marek Kulon
 *
 */

@Controller
public class DatabaseController {
	
	private static final Logger log = LoggerFactory.getLogger(DatabaseController.class);
	
	@RequestMapping(value="/database", method={GET, HEAD})
	protected String index(HttpServletRequest req, HttpServletResponse res) {
		return "database";
	}
	
	@RequestMapping(value="/database/new-game", method=GET)
	protected @ResponseBody ResponseMessage<?> newGame(HttpServletRequest req, HttpServletResponse res) {
		log.debug("received: {}", req.getParameterMap());

		final Integer maxIncorrectGuessesNo = parseOrNull(req.getParameter("maxIncorrectGuessesNo"));
		final Secret.Category category = Secret.Category.findByNameIgnoreCase(StringUtils.defaultString(req.getParameter("category")));
		
		if(maxIncorrectGuessesNo==null || maxIncorrectGuessesNo<0 || category==null) {
			log.info("illegal arguments recived: {}", req.getParameterMap());
			return ResponseMessages.newFailureMessage("ILLEGAL-ARGUMENTS");
		}
		
		Secret newSecret = SecretGenerator.generate(category);
		log.debug("generated secret: {}", newSecret);
		
		final String token = TokenGenerator.generate();
		log.debug("generated token: {}", token);
		
		final Game newGame = Game.newGame(maxIncorrectGuessesNo, newSecret);
		
		GameStateConfig.getGameStateRepository().saveOrUpdate(token, newGame.getGameState());
		
		return ResponseMessages.newSuccessMessage(new GameDtoResponse(token, newGame));
	}
	
	@RequestMapping(value="/database/load", method=GET)
	protected @ResponseBody ResponseMessage<?> load(HttpServletRequest req, HttpServletResponse res) {
		log.debug("received: {}", req.getParameterMap());

		final String token = req.getParameter("token");
		
		if(StringUtils.isEmpty(token)) {
			log.info("empty token: {}", req.getParameterMap());
			return ResponseMessages.newFailureMessage("EMPTY-TOKEN");
		}
		
		final GameState gs = GameStateConfig.getGameStateRepository().find(token);
		
		if(gs==null) {
			log.info("game not found: {}", req.getParameterMap());
			return ResponseMessages.newFailureMessage("GAME-NOT-FOUND");
		}
		
		return ResponseMessages.newSuccessMessage(new GameDtoResponse(token, Game.restore(gs)));
	}
	
	@RequestMapping(value="/database/guess", method=GET)
	protected @ResponseBody ResponseMessage<?> guess(HttpServletRequest req, HttpServletResponse res) throws IOException {
		log.debug("received: {}", req.getParameterMap());
		
		final String token = req.getParameter("token");
		
		if(StringUtils.isEmpty(token)) {
			log.info("illegal arguments recived: {}", req.getParameterMap());
			return ResponseMessages.newFailureMessage("EMPTY-TOKEN");
		}
		
		final Guess guess =  parseGuessOrNull(req.getParameter("guess"));
		
		if(guess==null) {
			log.info("illegal arguments recived: {}", req.getParameterMap());
			return ResponseMessages.newFailureMessage("ILLEGAL-ARGUMENTS");
		}
		
		// get old game state from db
		final GameState gs = GameStateConfig.getGameStateRepository().find(token);
		
		if(gs==null) {
			log.info("game not found: {}", req.getParameterMap());
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
	
	private Integer parseOrNull(String value) {
		try {
			return Integer.parseInt(value, 10);
		} catch(NumberFormatException nfe) {
			return null;
		}
	}
	
	private Guess parseGuessOrNull(String value) {
		return value!=null && value.length()==1 && Guess.isValidGuessCharacter(value.charAt(0)) ? 
				Guess.newFor(value.charAt(0)) : 
				null;
	}

}