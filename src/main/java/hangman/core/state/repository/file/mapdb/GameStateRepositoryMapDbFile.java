package hangman.core.state.repository.file.mapdb;

import hangman.config.GameStateConfig;
import hangman.core.state.GameState;
import hangman.core.state.repository.GameStateRepository;
import hangman.util.FileUtils;

import java.io.File;
import java.util.concurrent.ConcurrentNavigableMap;

import org.apache.commons.lang3.Validate;
import org.mapdb.DB;
import org.mapdb.DBMaker;


/**
 * Singleton by Bill Pugh
 * 
 * Reason for it is file locking - if two instances of MapDB are trying to open the same file 
 * db throws exception 
 * 
 * @see http://www.mapdb.org/faq-problems.html
 * 
 *  
 * @author Marek Kulon
 * 
 * @see documentation on http://www.mapdb.org/
 *
 */
public class GameStateRepositoryMapDbFile implements GameStateRepository {
	private final DB db;
	private final ConcurrentNavigableMap<String, GameState> map;
	
	
	@Override
	public GameState find(String token) {
		Validate.notNull(token);
		
		GameState gameState = map.get(token);
		
		return gameState;
	}

	@Override
	public void saveOrUpdate(String token, GameState value) {
		Validate.notNull(token);
		Validate.notNull(value);
		
		map.put(token, value);
		db.commit();
	}

	@Override
	public void remove(String token) {
		Validate.notNull(token);
		
		map.remove(token);
		db.commit();
	}
	
	
	
	public static GameStateRepositoryMapDbFile getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder {
		private static final GameStateRepositoryMapDbFile INSTANCE = new GameStateRepositoryMapDbFile();
	}
	
	private GameStateRepositoryMapDbFile() {
		File dbFile = FileUtils.getFileByPath(GameStateConfig.FILE_PATH);
		db = DBMaker.newFileDB(dbFile)
				.closeOnJvmShutdown()
				.encryptionEnable(GameStateConfig.PASSWORD)
				.make();
		map = db.getTreeMap(GameStateConfig.COLLECTION_NAME);
	}
}
