package hangman.web.exception;

public class IllegalAllowedIncorrectGuessesNumberException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public IllegalAllowedIncorrectGuessesNumberException(Integer value) {
        super("Invalid 'allowed incorrect guesses number' value [" + value + "]");
    }
}
