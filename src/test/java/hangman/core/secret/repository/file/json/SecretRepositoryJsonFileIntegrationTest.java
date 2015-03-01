package hangman.core.secret.repository.file.json;

import hangman.core.secret.Secret;
import hangman.core.secret.Secret.Category;
import hangman.core.secret.repository.SecretRepository;
import hangman.infrastructure.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
public class SecretRepositoryJsonFileIntegrationTest {

    @Autowired
    private SecretRepository secretRepository;

    @Test
    public void loadsFine() {
        List<Secret> secrets = secretRepository.findAllByCategory(Category.ANIMALS);

        assertNotNull(secrets);
        assertEquals(2, secrets.size());
        assertEquals(Secret.of("Monkey", Category.ANIMALS), secrets.get(0));
        assertEquals(Secret.of("Lion", Category.ANIMALS), secrets.get(1));
    }

}
