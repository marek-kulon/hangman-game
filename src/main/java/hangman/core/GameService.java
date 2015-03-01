package hangman.core;


import hangman.core.guess.Guess;
import hangman.core.secret.Secret;

import java.util.Optional;

/**
 * Collection of common operations regarding game object.
 * Operations modifying state of an object are guaranteed to save the result back into repository.
 */
public interface GameService {

    /**
     * Creates new game, generates its id and saves game in repository.
     *
     * @param category              category of a secret eg. ANIMALS, FRUITS
     * @param maxIncorrectGuessesNo maximum number or incorrect guesses user can make
     * @return pair of values: game id, game
     */
    IdGamePair createGame(Secret.Category category, int maxIncorrectGuessesNo);

    /**
     * Finds game in repository
     *
     * @param gameId id value assigned to game
     * @return found game or empty value if game wasn't found
     */
    Optional<Game> findGame(String gameId);

    /**
     * Finds game in repository, performs guess operation on it and saves result in repository
     *
     * @param gameId id value assigned to game
     * @param guess  value of guess made by user
     * @return game after performed operation or empty value if game wasn't found in repository
     */
    Optional<Game> makeAGuess(String gameId, Guess guess);


    /**
     * Wrapper object containing game and its id.
     */
    public static class IdGamePair {
        private final String gameId;
        private final Game game;

        public IdGamePair(String gameId, Game game) {
            this.gameId =gameId;
            this.game = game;
        }

        public String getGameId() {
            return gameId;
        }

        public Game getGame() {
            return game;
        }
    }
}
