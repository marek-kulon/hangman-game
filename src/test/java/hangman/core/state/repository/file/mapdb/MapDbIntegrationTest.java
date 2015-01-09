package hangman.core.state.repository.file.mapdb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import hangman.core.guess.Guess;
import hangman.core.secret.Secret;
import hangman.core.secret.Secret.Category;
import hangman.core.state.GameState;
import hangman.util.FileUtils;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentNavigableMap;

import org.junit.Before;
import org.junit.Test;
import org.mapdb.DB;
import org.mapdb.DBMaker;

public class MapDbIntegrationTest {
	
	private static final String path = "game-sate-db-test.db";
	private static final String pass = "password";
	private static final String collection = "gameState";
	private File file;
	
	private GameState gs1;
	private GameState gs2;
	private GameState gs3;
	
	
	@Before
	public void setUp () {
		file = FileUtils.getFileByPath(path);
		
		Set<Guess> guesses = new HashSet<>();
		guesses.add(Guess.newGuess('a'));
		
		gs1 = GameState.newGameState(2, Secret.newSecret("dog", Category.ANIMALS), guesses);
		gs2 = GameState.newGameState(2, Secret.newSecret("orange", Category.FRUITS), guesses);
		gs3 = GameState.newGameState(2, Secret.newSecret("carrot", Category.VEGETABLES), guesses);
		
		DB db = DBMaker.newFileDB(file)
				.closeOnJvmShutdown()
				.encryptionEnable(pass)
				.make();
		ConcurrentNavigableMap<String, GameState> map = db.getTreeMap(collection);
		map.put("1", gs1);
		map.put("2", gs2);
		db.commit();				// transaction commit
		map.put("3", gs3);
		db.rollback();				// transaction rollback
		db.close();
	}

	@Test
	public void test() {
		DB db = DBMaker.newFileDB(file)
				.closeOnJvmShutdown()
				.encryptionEnable(pass)
				.make();
		ConcurrentNavigableMap<String, GameState> map = db.getTreeMap(collection);
		assertEquals(gs1, map.get("1")); // check transaction commit
		assertEquals(gs2, map.get("2"));
		assertTrue(map.get("3")==null); // check transaction rollback
		db.close();
	}

}
