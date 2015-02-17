package hangman.web.exception;

public class IllegalMaxIncorrectGuessesNumberException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public IllegalMaxIncorrectGuessesNumberException(Integer value) {
        super("Invalid 'maximum incorrect guesses' value [" + value + "]");
    }
}
