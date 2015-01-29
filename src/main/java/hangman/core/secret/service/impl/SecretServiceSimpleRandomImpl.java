package hangman.core.secret.service.impl;

import hangman.core.secret.Secret;
import hangman.core.secret.repository.SecretRepository;
import hangman.core.secret.service.SecretService;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Resource;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SecretServiceSimpleRandomImpl implements SecretService {
	
	private static final Logger log = LoggerFactory.getLogger(SecretServiceSimpleRandomImpl.class);
	
	@Resource(name="secretRepository")
	private SecretRepository secretRepository;
	
	@Override
	public Secret getRandomByCategory(Secret.Category category) {
		Validate.notNull(category);
		
		final List<Secret> secrets = secretRepository.findAllByCategory(category);
		
		if (secrets==null || secrets.isEmpty()) {
			throw new IllegalStateException("no secrets from repository");
		}
		
		log.debug("secrets, size: {}", secrets.size());
		
		// faster than standard Random in multi-threaded env
		final Random rnd = ThreadLocalRandom.current(); 
		
		return secrets.get(rnd.nextInt(secrets.size())); 
	}
}
