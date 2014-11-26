package hangman.core.secret;

import hangman.config.SecretConfig;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.Validate;

/**
 * 
 * @author Marek Kulon
 *
 */
public class SecretGenerator {
	
	public static Secret generate(Secret.Category category) {
		Validate.notNull(category);
		
		final List<Secret> secrets = SecretConfig.getRepository().findAllByCategory(category);
		
		if (secrets==null || secrets.isEmpty()) {
			throw new IllegalStateException("no secrets from repository");
		}
		
		final Random rnd = ThreadLocalRandom.current(); // faster than standard Random in multi-threaded env
		
		return secrets.get(rnd.nextInt(secrets.size())); 
	}
	
	private SecretGenerator(){};
}
