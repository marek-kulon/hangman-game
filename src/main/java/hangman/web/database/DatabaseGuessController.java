package hangman.web.database;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import hangman.config.GameStateConfig;
import hangman.core.Game;
import hangman.core.guess.Guess;
import hangman.core.state.GameState;
import hangman.core.state.GuessAlreadyMadeException;
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
public class DatabaseGuessController {
	
	private static final Logger log = LoggerFactory.getLogger(DatabaseGuessController.class);
	
	@RequestMapping(value="/database/guess", method=GET)
	protected @ResponseBody ResponseMessage<?> doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
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
	
	private Guess parseGuessOrNull(String value) {
		return value!=null && value.length()==1 && Guess.isValidGuessCharacter(value.charAt(0)) ? 
				Guess.newFor(value.charAt(0)) : 
				null;
	}

}