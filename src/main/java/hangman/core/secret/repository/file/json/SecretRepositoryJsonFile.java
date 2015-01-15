package hangman.core.secret.repository.file.json;

import hangman.core.secret.Secret;
import hangman.core.secret.Secret.Category;
import hangman.core.secret.repository.SecretRepository;
import hangman.util.FileUtils;
import hangman.util.JsonConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonAnySetter;


/**
 * 
 * @author Marek Kulon
 *
 */
@Component("secretRepository")
public class SecretRepositoryJsonFile implements SecretRepository {
	
	private static final Logger log = LoggerFactory.getLogger(SecretRepositoryJsonFile.class);
	
	private final Map<Category, List<Secret>> storage = new HashMap<Secret.Category, List<Secret>>(); // do not expose it outside

	@Override
	public List<Secret> findAllByCategory(Category category) {
		Validate.notNull(category);
		return storage.get(category);
	}
	
	@Autowired // use this constructor to create bean
    private SecretRepositoryJsonFile(@Value("${secret.file-path}") String filePath) {
		log.info("loading secrets from file: {}", filePath);
		
		final String json = FileUtils.readFileToString(filePath);
		
		final JsonFileData data = JsonConverter.convertOrNull(json, JsonFileData.class);
		
		for(Category category: Category.values()) {
			final List<Secret> secrets = new ArrayList<Secret>();
			
			List<String> strSecrets = data.categoryToSecrets.get(category.name());
			if (strSecrets!=null) {
				for(String value : strSecrets) {
					secrets.add((Secret.newSecret(value, category)));
				}
			} else {
				log.warn("secrets for category missing, category: {}, file path: {}", category, filePath);
			}
			storage.put(category, secrets);
		}
    }

	private static class JsonFileData {
		Map<String, List<String>> categoryToSecrets = new HashMap<String, List<String>>();
		
		@JsonAnySetter 
		public void add(String key, List<String> values) {
			categoryToSecrets.put(key, values);
		}
	}
}
