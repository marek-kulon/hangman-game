package hangman.core.state;


public class IllegalGameStateException extends IllegalStateException {
	
	private static final long serialVersionUID = 401013054577670490L;

	public IllegalGameStateException(String message) {
		super(message);
	}

}
