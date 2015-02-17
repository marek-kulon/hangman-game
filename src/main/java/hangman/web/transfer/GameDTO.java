package hangman.web.transfer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import hangman.core.Game;
import hangman.core.Game.GameStatus;
import hangman.core.guess.Guess;
import hangman.core.state.GameState;
import hangman.web.util.GameStateUtils;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 * Game Data Transfer Object
 * Contains:
 * - game state
 * - guess word
 *
 * @author Marek Kulon
 */
public class GameDTO {

    private final Game game;

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
        public final GameState gameState;

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
            return gameState.getGuesses().stream()
                    .map(GuessDto::new)
                    .collect(toSet());
        }

        @JsonProperty("correctGuessesNo")
        public long getCorrectGuessesNo() {
            return gameState.getCorrectGuessesNo();
        }

        @JsonProperty("incorrectGuessesNo")
        public long getIncorrectGuessesNo() {
            return gameState.getIncorrectGuessesNo();
        }

        @JsonProperty("status")
        public GameStatus getStatus() {
            return gameState.getGameStatus();
        }


        private static class GuessDto {

            @JsonIgnore
            private final Guess guess;

            public GuessDto(Guess guess) {
                this.guess = guess;
            }

            @JsonCreator
            public GuessDto(@JsonProperty("value") char value) {
                this.guess = Guess.of(value);
            }

            @JsonProperty("value")
            public char getValue() {
                return guess.getValue();
            }
        }
    }

}
