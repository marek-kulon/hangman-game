package hangman.core.secret.service;

import hangman.core.secret.Secret;
import hangman.core.secret.Secret.Category;

public interface SecretService {

	Secret getRandomByCategory(Category category);

}
