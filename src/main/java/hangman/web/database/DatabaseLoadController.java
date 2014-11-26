package hangman.web.database;

import hangman.config.GameStateConfig;
import hangman.core.Game;
import hangman.core.state.GameState;
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

@WebServlet("/database/load")
public class DatabaseLoadController extends HttpServlet {
	
	private static final long serialVersionUID = 4574161195625136248L;
	private static final Logger log = LoggerFactory.getLogger(DatabaseLoadController.class);
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		log.debug("received: {}", req.getParameterMap());

		final String token = req.getParameter("token");
		
		if(StringUtils.isEmpty(token)) {
			log.info("empty token: {}", req.getParameterMap());
			ControllerUtils.sendJson(ResponseMessages.newFailureMessage("EMPTY-TOKEN"), res);
			return;
		}
		
		final GameState gs = GameStateConfig.getGameStateRepository().find(token);
		
		if(gs==null) {
			log.info("game not found: {}", req.getParameterMap());
			ControllerUtils.sendJson(ResponseMessages.newFailureMessage("GAME-NOT-FOUND"), res);
			return;
		}
		
		ControllerUtils.sendJson(ResponseMessages.newSuccessMessage(new GameDtoResponse(token, Game.restore(gs))), res);
	}

}