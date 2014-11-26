package hangman.core.secret.repository.file.json;

public class SecretRepositoryAlreadyLoaded extends IllegalStateException {

	private static final long serialVersionUID = 5580692563560786066L;
	
	public SecretRepositoryAlreadyLoaded(String message) {
		super(message);
	}
	
}
