package hangman.core.secret.repository.file.json;

public class SecretRepositoryAlreadyLoaded extends IllegalStateException {

	private static final long serialVersionUID = 1L;
	
	public SecretRepositoryAlreadyLoaded(String message) {
		super(message);
	}
	
}
