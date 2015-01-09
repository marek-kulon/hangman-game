package hangman.core.secret;

import hangman.core.guess.Guess;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.Validate;

public final class Secret implements Serializable {
	private static final long serialVersionUID = 1L;

	/** Space separator between words */
	public static final char SPACE_SEPARATOR = ' ';
	
	/** Unmodifiable set of allowed characters */
	public static final Set<Character> ALLOWED_CHARACTERS = Collections.unmodifiableSet(new HashSet<Character>() {
		private static final long serialVersionUID = 1L;
		{
			// enums would be slight over-engineering
			for(char allowed='a'; allowed<='z'; allowed++) {
				add(allowed);
			}
			for(char allowed='A'; allowed<='Z'; allowed++) {
				add(allowed);
			}
		}
	});
	

	private final String value;
	private final Category category;

	public String getValue() {
		return value;
	}

	public Category getCategory() {
		return category;
	}
	
	/**
	 * 
	 * @return how many guess does it take to know the secret
	 */
	public int getGuessesToKnowMeNo() {
		final Set<Guess> uniqueGuesses = new HashSet<>();
		for(char c : getValue().toCharArray()) {
			if (Guess.isValidGuessCharacter(c)) { // be careful about space separators
				uniqueGuesses.add(Guess.newGuess(c));
			}
		}
		return uniqueGuesses.size();
	}
	
	public static boolean isValidSecretCharacter(char value) {
		return value == SPACE_SEPARATOR || ALLOWED_CHARACTERS.contains(value);
	}
	
	// for convenience
	public static Secret newSecret(String value, Category category){
		return new Secret(value, category);
	}
	
	// private constructor
	private Secret(String value, Category category) {
		Validate.notBlank(value);
		Validate.notNull(category);

		this.value = value;
		this.category = category;
	}
	
	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((category == null) ? 0 : category.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (obj.getClass() != getClass()) {
			return false;
		}
		Secret other = (Secret) obj;
		if (category != other.category) {
			return false;
		}
		if (value == null) {
			if (other.value != null) { return false; }
		} else if (!value.equals(other.value)) {
			return false;
		}
			
		return true;
	}




	/**
	 * Available categories
	 */
	public static enum Category {
		ANIMALS, FRUITS, VEGETABLES;
		
		public static Category getByNameIgnoreCase(String value) {
			Validate.notNull(value);
			
			for(Category category: Category.values()) {
				if(category.name().equalsIgnoreCase(value)) {
					return category;
				}
			}
			return null;
		}
	}


	@Override
	public String toString() {
		return "Secret [value=" + value + ", category=" + category + "]";
	}
	
	
}
