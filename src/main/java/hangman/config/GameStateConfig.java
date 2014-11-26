package hangman.config;

import hangman.core.state.repository.GameStateRepository;
import hangman.core.state.repository.file.mapdb.GameStateRepositoryMapDbFile;



/**
 * Configuration for db
 * 
 * In real live scenario I would use dependency injection instead of static class
 * 
 * 
 * @author Marek Kulon
 *
 */
public final class GameStateConfig {
	// TODO externalise configuration to application.properties file
	public static final String FILE_PATH = "game-sate-db-live.db";
	public static final String PASSWORD = "password";
	public static final String COLLECTION_NAME = "gameState";
	
	
	public static GameStateRepository getGameStateRepository() {
		return GameStateRepositoryMapDbFile.getInstance();
	}
	
	// not to create instances
	private GameStateConfig() {}
}
