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

import com.fasterxml.jackson.annotation.JsonAnySetter;


/**
 * Read json file in thread safe manner and stores results in memory
 * 
 * 
 * WARNING:
 * Couldn't use better singleton by Bill Pugh because I want to pass *filePath* parameter there so I could test the code
 * Instead I'm using slowest thread safe solution that does the job for current application
 * 
 * I decided to do it because:
 * 1: in real live scenario I would use 'dev'/'test'/'live' environment parameters and dependency injection 
 *    instead of singletons,
 * 2: using faster, double 'locking & volatile' solution in current set up would be wrong since it would introduce silent errors:
 *    calling *loadFromFile* with different *filePath* parameter would not have an effect on internal storage 
 * 3: using double 'locking & volatile & map<fileName, storage>' would be over-engineering because
 *    I would also have to change *find...* methods to pass extra *filePath* parameter
 * 
 * @author Marek Kulon
 *
 */
public final class SecretRepositoryJsonFile implements SecretRepository {
	
	private static final Logger log = LoggerFactory.getLogger(SecretRepositoryJsonFile.class);
	private static SecretRepositoryJsonFile INSTANCE = null;
	private final Map<Category, List<Secret>> storage = new HashMap<Secret.Category, List<Secret>>(); // do not expose it outside
	private final String filePath;
	/**
	 * @param filePath
	 * @return
	 */
	public static synchronized SecretRepositoryJsonFile loadFromFile(String filePath) {
		Validate.notBlank(filePath);
		
		if (INSTANCE != null) {
			if (!INSTANCE.getFilePath().equals(filePath)) {
				throw new SecretRepositoryAlreadyLoaded("repository is already loaded from ["+INSTANCE.getFilePath()+"]");
			}
		}
		if (INSTANCE == null) {
			INSTANCE = new SecretRepositoryJsonFile(filePath);
		}
		return INSTANCE;
	}
	
	// private constructor
	private SecretRepositoryJsonFile(String filePath) {
		
		this.filePath = filePath;
		
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


	@Override
	public List<Secret> findAllByCategory(Category category) {
		Validate.notNull(category);
		return storage.get(category);
	}
	
	public String getFilePath() {
		return filePath;
	}

	private static class JsonFileData {
		Map<String, List<String>> categoryToSecrets = new HashMap<String, List<String>>();
		
		@JsonAnySetter 
		public void add(String key, List<String> values) {
			categoryToSecrets.put(key, values);
		}
	}
}
