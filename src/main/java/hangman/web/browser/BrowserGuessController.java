package hangman.web.browser;

import hangman.core.Game;
import hangman.core.guess.Guess;
import hangman.core.state.GuessAlreadyMadeException;
import hangman.util.JsonConverter;
import hangman.web.browser.dto.GameDtoRequest;
import hangman.web.browser.dto.GameDtoResponse;
import hangman.web.common.response.ResponseMessages;
import hangman.web.util.ControllerUtils;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Marek Kulon
 *
 */
@WebServlet("/browser/guess")
public class BrowserGuessController extends HttpServlet {
	
	private static final long serialVersionUID = 2181968587693124160L;
	private static final Logger log = LoggerFactory.getLogger(BrowserGuessController.class);
	

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		log.debug("received: {}", req.getParameterMap());
		
		final GameDtoRequest gameDto = parseGameDtoOrNull(req.getParameter("state"));
		final Guess guess =  parseGuessOrNull(req.getParameter("guess"));
		
		if(gameDto==null || guess==null) {
			log.info("illegal arguments recived: {}", req.getParameterMap());
			ControllerUtils.sendJson(ResponseMessages.newFailureMessage("ILLEGAL-ARGUMENTS"), res);
			return;
		}
		
		if(gameDto.isCorrupted()) {
			log.info("corrupted data: {}", req.getParameterMap());
			ControllerUtils.sendJson(ResponseMessages.newFailureMessage("CORRUPTED-DATA"), res);
			return;
		}
		
		final Game game = Game.restore(gameDto.getGameState());

		try {
			boolean isCorrect = game.doGuess(guess);
			log.debug("isCorrect: {}", isCorrect);
		} catch (GuessAlreadyMadeException e) {
			ControllerUtils.sendJson(ResponseMessages.newFailureMessage("ALREADY-GUESSED"), res);
			return;
		}
		ControllerUtils.sendJson(ResponseMessages.newSuccessMessage(new GameDtoResponse(game)), res);
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
