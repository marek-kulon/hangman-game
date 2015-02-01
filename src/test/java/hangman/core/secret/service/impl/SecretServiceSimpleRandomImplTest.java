package hangman.core.secret.service.impl;

import hangman.core.secret.Secret;
import hangman.core.secret.repository.SecretRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SecretServiceSimpleRandomImplTest {

    @Mock
    private SecretRepository secretRepository;
    @InjectMocks
    private SecretServiceSimpleRandomImpl service;


    @Test(expected = IllegalStateException.class)
    public void throwExcOnNoSecretsFound() throws Exception {
        when(secretRepository.findAllByCategory(Secret.Category.ANIMALS)).thenReturn(null);

        service.getRandomByCategory(Secret.Category.ANIMALS);
    }

    @Test
    public void generateOne() throws Exception {
        final Secret testSecret = Secret.of("test", Secret.Category.ANIMALS);
        final List<Secret> secrets = Arrays.asList(testSecret);

        when(secretRepository.findAllByCategory(Secret.Category.ANIMALS)).thenReturn(secrets);

        Secret generated = service.getRandomByCategory(Secret.Category.ANIMALS);
        assertEquals(testSecret, generated);
    }
}