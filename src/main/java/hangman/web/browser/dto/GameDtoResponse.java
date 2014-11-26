package hangman.web.browser.dto;

import hangman.core.Game;
import hangman.web.util.GameStateUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO definition of response
 * Contains:
 * - game state
 * - check sum generated on the run
 * - guess word
 * 
 * @author Marek Kulon
 *
 */
public class GameDtoResponse {
	
	private Game game; // it's response: no getters required
	
	public GameDtoResponse(Game game) {
		this.game = game;
	}
	
	@JsonProperty("state")
	public GameStateDto getGameState() {
		return new GameStateDto(game.getGameState());
	}
	
	@JsonProperty("checkSum")
	public String getCheckSum() {
		return GameStateUtils.generateCheckSum(game.getGameState());
	}
	
	@JsonProperty("guessedValue")
	public String getGuessedValue() {
		return GameStateUtils.getGuessedValue(game.getGameState(), '_');
	}
	
}
