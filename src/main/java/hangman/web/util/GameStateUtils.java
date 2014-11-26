package hangman.web.util;

import hangman.core.GameStatus;
import hangman.core.guess.Guess;
import hangman.core.secret.Secret;
import hangman.core.state.GameState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

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
			boolean useOriginal = character==Secret.SPACE_SEPARATOR || guesses.contains(Guess.newFor(character)); //check for space before creating new guess
			sb.append(useOriginal ? character : notGuessedCharacterReplacement);
		}
		return sb.toString();
	}
	
	public static String generateCheckSum(GameState state) {
		Validate.notNull(state);
		
		final String source = StringUtils.join(
			"#",
			String.valueOf(state.getMaxIncorrectGuessesNo()),
			"#",
			state.getSecret().getValue(),
			"#",
			state.getSecret().getCategory().name(),
			"#",
			getOrderedGuesses(state.getGuesses())
		);
		return MD5Util.generate(source);
	}
	
	
	private static String getOrderedGuesses(Set<Guess> values){
		Validate.noNullElements(values);
		
		List<Guess> list = new ArrayList<>(values);
		Collections.sort(list);
		StringBuilder sb = new StringBuilder(list.size());
		for(Guess g : list) {
			sb.append(g.getValue());
		}
		return sb.toString();
	}

}
