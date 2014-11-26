package hangman.web.browser.dto;

import hangman.core.state.GameState;
import hangman.web.util.GameStateUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *  DTO definition of request
 *  Contains:
 *  - game state
 *  - saved check sum
 * 
 * @author Marek Kulon
 *
 */
public class GameDtoRequest {
	
	private GameState gameState;
	private String checkSum;
	
	@JsonCreator
	public GameDtoRequest (
			@JsonProperty("state") GameStateDto state,
			@JsonProperty("checkSum") String checkSum) {
		this.gameState = state.gameState;
		this.checkSum = checkSum;
	}
	
	public String getCheckSum() {
		return checkSum;
	}

	public GameState getGameState() {
		return gameState;
	}

	public boolean isCorrupted() {
		final String generated = GameStateUtils.generateCheckSum(gameState);
		return !generated.equals(getCheckSum());
	}
}
