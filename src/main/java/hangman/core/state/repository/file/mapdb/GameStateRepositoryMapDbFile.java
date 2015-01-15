package hangman.core.state.repository.file.mapdb;

import hangman.core.state.GameState;
import hangman.core.state.repository.GameStateRepository;
import hangman.util.FileUtils;

import java.io.File;
import java.util.concurrent.ConcurrentNavigableMap;

import org.apache.commons.lang3.Validate;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;


/**
 * 
 * @author Marek Kulon
 * 
 * @see documentation on http://www.mapdb.org/
 *
 */
@Repository("gameStateRepository")
public class GameStateRepositoryMapDbFile implements GameStateRepository {
	private static final Logger log = LoggerFactory.getLogger(GameStateRepositoryMapDbFile.class);
	
	private final DB db;
	private final ConcurrentNavigableMap<String, GameState> map;
	
	
	@Override
	public GameState find(String gameId) {
		Validate.notNull(gameId);
		
		GameState gameState = map.get(gameId);
		
		return gameState;
	}

	@Override
	public void saveOrUpdate(String gameId, GameState value) {
		Validate.notNull(gameId);
		Validate.notNull(value);
		
		map.put(gameId, value);
		db.commit();
	}

	@Override
	public void remove(String gameId) {
		Validate.notNull(gameId);
		
		map.remove(gameId);
		db.commit();
	}
	
	@Autowired // use this constructor to create bean
	private GameStateRepositoryMapDbFile(
			@Value("${dbmap.file-path}") String filePath,
			@Value("${dbmap.password}") String password,
			@Value("${dbmap.collection-name}") String collectionName
		) {
		
		File dbFile = FileUtils.getFileByPath(filePath);
		db = DBMaker.newFileDB(dbFile)
				.closeOnJvmShutdown()
				.encryptionEnable(password)
				.make();
		map = db.getTreeMap(collectionName);
		
		log.info("dbmap created, file: {}", filePath);
	}
}
