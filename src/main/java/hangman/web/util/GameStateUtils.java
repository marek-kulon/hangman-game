package hangman.web.util;

import hangman.core.Game.GameStatus;
import hangman.core.guess.Guess;
import hangman.core.secret.Secret;
import hangman.core.state.GameState;
import org.apache.commons.lang3.Validate;

import java.util.Set;

public class GameStateUtils {
	
	public static String getGuessedValue(GameState gameState, char notGuessedCharacterReplacement) {
		Validate.notNull(gameState);
		// make sure it doesn't overlap with one of allowed secret characters
		Validate.isTrue(!Secret.isValidSecretCharacter(notGuessedCharacterReplacement));
		
		// show secret if game is finished
		if (!gameState.getGameStatus().equals(GameStatus.IN_PROGRESS)) {
			return gameState.getSecret().getValue();
		}
		
		// replace unknown characters with notGuessedCharacterReplacement
		final Secret secret = gameState.getSecret();
		final Set<Guess> guesses = gameState.getGuesses();
		final StringBuilder sb = new StringBuilder();
		for(int i=0; i<secret.getValue().length(); i++) {
			char character = secret.getValue().charAt(i);
			boolean useOriginal = character==Secret.SPACE_SEPARATOR || guesses.contains(Guess.of(character)); //check for space before creating new guess
			sb.append(useOriginal ? character : notGuessedCharacterReplacement);
		}
		return sb.toString();
	}

}
