package hangman.web.browser.dto;

import hangman.core.GameStatus;
import hangman.core.guess.Guess;
import hangman.core.secret.Secret;
import hangman.core.secret.Secret.Category;
import hangman.core.state.GameState;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * DTO definition of game state
 * 
 * @author Marek Kulon
 *
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class GameStateDto {
	
	@JsonIgnore
	public GameState gameState;
	
	public GameStateDto(GameState gameState) {
		this.gameState = gameState;
	}

	@JsonProperty("maxIncorrectGuessesNo")
	public int getMaxIncorrectGuessesNo() {
		return gameState.getMaxIncorrectGuessesNo();
	}
	
	@JsonProperty("secret")
	public SecretDto getSecret() {
		return new SecretDto(gameState.getSecret());
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

	@JsonCreator
	public GameStateDto(
			@JsonProperty("maxIncorrectGuessesNo") int maxIncorrectGuessesNo, 
			@JsonProperty("secret") SecretDto secret,
			@JsonProperty("guesses") Set<GuessDto> guessesDto) {
		
		Set<Guess> guesses = new HashSet<>();
		for(GuessDto guessDto: guessesDto) {
			guesses.add(guessDto.guess);
		}
		
		this.gameState = GameState.newGameState(maxIncorrectGuessesNo, secret.secret, guesses);
	}


	private static class SecretDto {
		
		@JsonIgnore
		private Secret secret;
		
		public SecretDto(Secret secret) {
			this.secret = secret;
		}
		
		@JsonProperty("value")
		public String getValue() {
			return secret.getValue();
		}
		
		@JsonProperty("category")
		public String getCategory() {
			return secret.getCategory().name();
		}
		
		@JsonCreator
		public SecretDto(
				@JsonProperty("value") String value, 
				@JsonProperty("category") String category) {
			this.secret = Secret.newSecret(value, Category.findByNameIgnoreCase(category));
		}
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
			this.guess = Guess.newFor(value);
		}
	}

}
