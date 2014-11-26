package hangman.web.database;

import hangman.config.GameStateConfig;
import hangman.core.Game;
import hangman.core.secret.Secret;
import hangman.core.secret.SecretGenerator;
import hangman.core.state.repository.util.TokenGenerator;
import hangman.web.common.response.ResponseMessages;
import hangman.web.database.dto.GameDtoResponse;
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

@WebServlet("/database/new-game")
public class DatabaseNewGameController extends HttpServlet {
	
	private static final long serialVersionUID = 4477168749778182378L;
	private static final Logger log = LoggerFactory.getLogger(DatabaseNewGameController.class);
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		log.debug("received: {}", req.getParameterMap());

		final Integer maxIncorrectGuessesNo = parseOrNull(req.getParameter("maxIncorrectGuessesNo"));
		final Secret.Category category = Secret.Category.findByNameIgnoreCase(req.getParameter("category"));
		
		if(maxIncorrectGuessesNo==null || maxIncorrectGuessesNo<0 || category==null) {
			log.info("illegal arguments recived: {}", req.getParameterMap());
			ControllerUtils.sendJson(ResponseMessages.newFailureMessage("ILLEGAL-ARGUMENTS"), res);
			return;
		}
		
		Secret newSecret = SecretGenerator.generate(category);
		log.debug("generated secret: {}", newSecret);
		
		final String token = TokenGenerator.generate();
		log.debug("generated token: {}", token);
		
		final Game newGame = Game.newGame(maxIncorrectGuessesNo, newSecret);
		
		GameStateConfig.getGameStateRepository().saveOrUpdate(token, newGame.getGameState());
		
		ControllerUtils.sendJson(ResponseMessages.newSuccessMessage(new GameDtoResponse(token, newGame)), res);
	}
	
	
	private Integer parseOrNull(String value) {
		try {
			return Integer.parseInt(value, 10);
		} catch(NumberFormatException nfe) {
			return null;
		}
	}

}