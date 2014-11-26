package hangman.web.database;

import hangman.config.GameStateConfig;
import hangman.core.Game;
import hangman.core.guess.Guess;
import hangman.core.state.GameState;
import hangman.core.state.GuessAlreadyMadeException;
import hangman.web.common.response.ResponseMessages;
import hangman.web.database.dto.GameDtoResponse;
import hangman.web.util.ControllerUtils;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Marek Kulon
 *
 */

@WebServlet("/database/guess")
public class DatabaseGuessController extends HttpServlet {
	
	private static final long serialVersionUID = 5255208685390578865L;
	private static final Logger log = LoggerFactory.getLogger(DatabaseGuessController.class);
	

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		log.debug("received: {}", req.getParameterMap());
		
		final String token = req.getParameter("token");
		
		if(StringUtils.isEmpty(token)) {
			log.info("illegal arguments recived: {}", req.getParameterMap());
			ControllerUtils.sendJson(ResponseMessages.newFailureMessage("EMPTY-TOKEN"), res);
			return;
		}
		
		final Guess guess =  parseGuessOrNull(req.getParameter("guess"));
		
		if(guess==null) {
			log.info("illegal arguments recived: {}", req.getParameterMap());
			ControllerUtils.sendJson(ResponseMessages.newFailureMessage("ILLEGAL-ARGUMENTS"), res);
			return;
		}
		
		// get old game state from db
		final GameState gs = GameStateConfig.getGameStateRepository().find(token);
		
		if(gs==null) {
			log.info("game not found: {}", req.getParameterMap());
			ControllerUtils.sendJson(ResponseMessages.newFailureMessage("GAME-NOT-FOUND"), res);
			return;
		}
		
		final Game game = Game.restore(gs);

		try {
			boolean isCorrect = game.doGuess(guess);
			log.debug("isCorrect: {}", isCorrect);
		} catch (GuessAlreadyMadeException e) {
			ControllerUtils.sendJson(ResponseMessages.newFailureMessage("ALREADY-GUESSED"), res);
			return;
		}
		
		// save result back to db
		GameStateConfig.getGameStateRepository().saveOrUpdate(token, game.getGameState());
		
		ControllerUtils.sendJson(ResponseMessages.newSuccessMessage(new GameDtoResponse(token, game)), res);
	}
	
	private Guess parseGuessOrNull(String value) {
		return value!=null && value.length()==1 && Guess.isValidGuessCharacter(value.charAt(0)) ? 
				Guess.newFor(value.charAt(0)) : 
				null;
	}

}