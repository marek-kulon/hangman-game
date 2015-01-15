package hangman.web.exception;

public class SecretCategoryNotSupportedException  extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public SecretCategoryNotSupportedException(String value) {
		super("Secret category not supported by system ["+value+"]");
	}
}
