package hangman.web.exception;

public class GameNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public GameNotFoundException(String gameId) {
        super("Game not found [" + gameId + "]");
    }
}
