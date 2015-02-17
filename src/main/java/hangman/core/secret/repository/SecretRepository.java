package hangman.core.secret.repository;

import hangman.core.secret.Secret;
import hangman.core.secret.Secret.Category;

import java.util.List;

public interface SecretRepository {

    List<Secret> findAllByCategory(Category category);

}
