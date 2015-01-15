package hangman.core.state;


public class IllegalGameStateException extends IllegalStateException {
	
	private static final long serialVersionUID = 1L;

	public IllegalGameStateException(String message) {
		super(message);
	}

}
