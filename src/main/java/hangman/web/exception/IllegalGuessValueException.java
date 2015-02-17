package hangman.web.exception;

public class IllegalGuessValueException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public IllegalGuessValueException(char value) {
        super("Invalid guess value [" + value + "]");
    }
}
