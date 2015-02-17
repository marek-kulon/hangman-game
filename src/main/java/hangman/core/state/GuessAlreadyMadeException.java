package hangman.core.state;

import hangman.core.guess.Guess;


public class GuessAlreadyMadeException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    public GuessAlreadyMadeException(Guess value) {
        super("Value [" + value.getValue() + "] has already been used");
    }
}
