package hangman.web.database;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import hangman.config.GameStateConfig;
import hangman.core.Game;
import hangman.core.state.GameState;
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
public class DatabaseLoadController {
	
	private static final Logger log = LoggerFactory.getLogger(DatabaseLoadController.class);
	
	@RequestMapping(value="/database/load", method=GET)
	protected @ResponseBody ResponseMessage<?> doGet(HttpServletRequest req, HttpServletResponse res) {
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

}