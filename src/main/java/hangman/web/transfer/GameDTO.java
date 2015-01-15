package hangman.web.transfer;

import hangman.core.Game;
import hangman.core.GameStatus;
import hangman.core.guess.Guess;
import hangman.core.state.GameState;
import hangman.web.util.GameStateUtils;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Game Data Transfer Object
 * Contains:
 * - game state
 * - guess word
 * 
 * @author Marek Kulon
 *
 */
public class GameDTO {
	
	private Game game;
	
	public GameDTO(Game game) {
		this.game = game;
	}

	@JsonProperty("state")
	public GameStateDto getGameState() {
		return new GameStateDto(game.getGameState());
	}
	
	@JsonProperty("guessedValue")
	public String getGuessedValue() {
		return GameStateUtils.getGuessedValue(game.getGameState(), '_');
	}
	
	
	private static class GameStateDto {
		
		@JsonIgnore
		public GameState gameState;
		
		public GameStateDto(GameState gameState) {
			this.gameState = gameState;
		}

		@JsonProperty("maxIncorrectGuessesNo")
		public int getMaxIncorrectGuessesNo() {
			return gameState.getMaxIncorrectGuessesNo();
		}
		
		@JsonProperty("category")
		public String getCategory() {
			return gameState.getSecret().getCategory().name().toLowerCase();
		}
		
		@JsonProperty("guesses")
		public Set<GuessDto> getGuesses() {
			
			Set<GuessDto> guesses = new HashSet<>();
			for(Guess guess : gameState.getGuesses()) {
				guesses.add(new GuessDto(guess));
			}
			
			return guesses;
		}
		
		@JsonProperty("correctGuessesNo")
		public int getCorrectGuessesNo() {
			return gameState.getCorrectGuessesNo();
		}
		
		@JsonProperty("incorrectGuessesNo")
		public int getIncorrectGuessesNo() {
			return gameState.getIncorrectGuessesNo();
		}
		
		@JsonProperty("status")
		public GameStatus getStatus() {
			return gameState.getGameStatus();
		}

		
		private static class GuessDto {
			
			@JsonIgnore
			private Guess guess;
			
			public GuessDto(Guess guess) {
				this.guess = guess;
			}
			
			@JsonProperty("value")
			public char getValue() {
				return guess.getValue();
			}
			
			@JsonCreator
			public GuessDto(@JsonProperty("value") char value) {
				this.guess = Guess.newGuess(value);
			}
		}
	}
	
}
