package hangman.config;

import hangman.core.secret.repository.SecretRepository;
import hangman.core.secret.repository.file.json.SecretRepositoryJsonFile;


public final class SecretConfig {
	
	public static SecretRepository getRepository() {
		return SecretRepositoryJsonFile.loadFromFile("secrets-live.json");
	}
	
	private SecretConfig() {}
}
