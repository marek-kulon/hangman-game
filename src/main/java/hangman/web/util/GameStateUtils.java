package hangman.web.util;

import hangman.core.Game.GameStatus;
import hangman.core.guess.Guess;
import hangman.core.secret.Secret;
import hangman.core.state.GameState;

import static java.util.stream.IntStream.range;
import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;

public class GameStateUtils {
	
	public static String getGuessedValue(GameState gameState, char notGuessedCharacterReplacement) {
		notNull(gameState);
		// make sure it doesn't overlap with one of allowed secret characters
		isTrue(!Secret.isValidSecretCharacter(notGuessedCharacterReplacement));
		
		// show secret if game is finished
		if (!gameState.getGameStatus().equals(GameStatus.IN_PROGRESS)) {
			return gameState.getSecret().getValue();
		}
		
		// replace unknown characters with notGuessedCharacterReplacement
		final StringBuilder sb = new StringBuilder();
        range(0, gameState.getSecret().getValue().length()).forEach( i -> {
            char character = gameState.getSecret().getValue().charAt(i);
            // display space/guessed character
            boolean useOriginal = character==Secret.SPACE_SEPARATOR || gameState.getGuesses().contains(Guess.of(character));
            sb.append(useOriginal ? character : notGuessedCharacterReplacement);
        });
		return sb.toString();
	}

}
