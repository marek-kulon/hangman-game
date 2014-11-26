package hangman.core.state;

import hangman.core.guess.Guess;


public class GuessAlreadyMadeException extends Exception {
	
	private static final long serialVersionUID = 3750102312498459493L;

	public GuessAlreadyMadeException(Guess value) {
		super("value ["+value.getValue()+"] has already been used");
	}
}
