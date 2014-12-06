package hangman.web.database;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import hangman.config.GameStateConfig;
import hangman.core.Game;
import hangman.core.secret.Secret;
import hangman.core.secret.SecretGenerator;
import hangman.core.state.repository.util.TokenGenerator;
import hangman.web.common.response.ResponseMessage;
import hangman.web.common.response.ResponseMessages;
import hangman.web.database.dto.GameDtoResponse;

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
public class DatabaseNewGameController {
	
	private static final Logger log = LoggerFactory.getLogger(DatabaseNewGameController.class);
	
	@RequestMapping(value="/database/new-game", method=GET)
	protected @ResponseBody ResponseMessage<?> doGet(HttpServletRequest req, HttpServletResponse res) {
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
	
	
	private Integer parseOrNull(String value) {
		try {
			return Integer.parseInt(value, 10);
		} catch(NumberFormatException nfe) {
			return null;
		}
	}

}