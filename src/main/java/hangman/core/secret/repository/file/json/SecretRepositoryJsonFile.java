package hangman.core.secret.repository.file.json;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import hangman.core.secret.Secret;
import hangman.core.secret.Secret.Category;
import hangman.core.secret.repository.SecretRepository;
import hangman.util.FileUtils;
import hangman.util.JsonConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * 
 * @author Marek Kulon
 *
 */
@Component("secretRepository")
public class SecretRepositoryJsonFile implements SecretRepository {
	
	private static final Logger log = LoggerFactory.getLogger(SecretRepositoryJsonFile.class);

    // do not expose it outside
	private final Map<Category, List<Secret>> storage;

	@Autowired // use this constructor to create bean
    private SecretRepositoryJsonFile(@Value("${secret.file-path}") String filePath) {
		log.info("loading secrets from file: {}", filePath);
		
		final String json = FileUtils.readFileToString(filePath);
		final JsonFileData data = JsonConverter.convertOrNull(json, JsonFileData.class);

        storage = new HashMap<>();
        // convert string based data into object based one
        notNull(data).categoryToSecrets.forEach((categoryRaw, secretsRaw) -> {  // notNull makes it clear which part failed in case it happens
            Category category = Category.getByNameIgnoreCase(categoryRaw).get();
            List<Secret> secrets = secretsRaw.stream().map(s -> Secret.of(s, category)).collect(toList());
            storage.put(category, Collections.unmodifiableList(secrets));
        });
    }

    @Override
    public List<Secret> findAllByCategory(Category category) {
        notNull(category);
        return storage.get(category);
    }

    private static class JsonFileData {
		final Map<String, List<String>> categoryToSecrets = new HashMap<>();
		
		@JsonAnySetter 
		public void add(String key, List<String> values) {
			categoryToSecrets.put(key, values);
		}
	}
}
