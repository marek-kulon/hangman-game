package hangman.util;

import java.io.IOException;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * 
 * @author Marek Kulon
 *
 */
public class JsonConverter {
	
	private static final Logger log = LoggerFactory.getLogger(JsonConverter.class);

	
	public static String convert(Object value) throws JsonProcessingException {
		Validate.notNull(value);
		
		return new ObjectMapper().writeValueAsString(value);
	}
	
	public static String convertOrNull(Object value) {
		Validate.notNull(value); // want to validate here anyway: exception change in catch may hide error
		
		try {
			return convert(value);
		} catch (JsonProcessingException e) {
			log.error("serializing obj exc, {}", e);
			return null;
		}
	} 

	
	public static <T> T convert(String value, Class<T> valueType)
			throws JsonParseException, JsonMappingException, IOException {
		Validate.notNull(value);
		Validate.notNull(valueType);
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
		return mapper.readValue(value, valueType);
	}
	
	
	public static <T> T convertOrNull(String value, Class<T> valueType) {
		Validate.notNull(value); // want to validate here anyway: exception change in catch may hide error
		Validate.notNull(valueType);
		
		try {
			return convert(value, valueType);
		} catch (IOException e) {
			log.error("parsing json string exc, {}", e);
			return null;
		}
	}
}
