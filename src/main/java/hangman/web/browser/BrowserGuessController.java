package hangman.web.browser;

import static org.springframework.web.bind.annotation.RequestMethod.POST;
import hangman.core.Game;
import hangman.core.guess.Guess;
import hangman.core.state.GuessAlreadyMadeException;
import hangman.util.JsonConverter;
import hangman.web.browser.dto.GameDtoRequest;
import hangman.web.browser.dto.GameDtoResponse;
import hangman.web.common.response.ResponseMessage;
import hangman.web.common.response.ResponseMessages;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
public class BrowserGuessController {
	
	private static final Logger log = LoggerFactory.getLogger(BrowserGuessController.class);
	
	@RequestMapping(value="/browser/guess", method=POST)
	protected @ResponseBody ResponseMessage<?> doPost(HttpServletRequest req, HttpServletResponse res) {
		log.debug("received: {}", req.getParameterMap());
		
		final GameDtoRequest gameDto = parseGameDtoOrNull(req.getParameter("state"));
		final Guess guess =  parseGuessOrNull(req.getParameter("guess"));
		
		if(gameDto==null || guess==null) {
			log.info("illegal arguments recived: {}", req.getParameterMap());
			return ResponseMessages.newFailureMessage("ILLEGAL-ARGUMENTS");
		}
		
		if(gameDto.isCorrupted()) {
			log.info("corrupted data: {}", req.getParameterMap());
			return ResponseMessages.newFailureMessage("CORRUPTED-DATA");
		}
		
		final Game game = Game.restore(gameDto.getGameState());

		try {
			boolean isCorrect = game.doGuess(guess);
			log.debug("isCorrect: {}", isCorrect);
		} catch (GuessAlreadyMadeException e) {
			return ResponseMessages.newFailureMessage("ALREADY-GUESSED");
		}
		return ResponseMessages.newSuccessMessage(new GameDtoResponse(game));
	}
	
	private GameDtoRequest parseGameDtoOrNull(String value) {
		try {
			return JsonConverter.convert(value, GameDtoRequest.class);
		} catch (IOException e) {
			log.info("exception:", e);
			return null;
		}
	}
	
	private Guess parseGuessOrNull(String value) {
		return value!=null && value.length()==1 && Guess.isValidGuessCharacter(value.charAt(0)) ? 
				Guess.newFor(value.charAt(0)) : 
				null;
	}

}
